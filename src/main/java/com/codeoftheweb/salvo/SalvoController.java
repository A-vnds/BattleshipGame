package com.codeoftheweb.salvo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SalvoController {


    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GamePlayerRepository gamePlayerRepository;

    @Autowired
    private ShipRepository shipRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    @Autowired
    private SalvoRepository salvoRepository;


    @RequestMapping("/players")
    public Player getCurrentPlayer(Authentication authentication) {
        return playerRepository.findByUserName(authentication.getName());
    }

    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }


    @RequestMapping(path = "/create-account", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewPlayer(@RequestParam String userName, String password) {

        if (userName.isEmpty()) {
            return new ResponseEntity<>(makeMap("error", "No name, please enter a username"), HttpStatus.FORBIDDEN); //403
        }
        Player user = playerRepository.findByUserName(userName);
        if (user != null) {
            return new ResponseEntity<>(makeMap("error", "The username: " + userName + " already exists"), HttpStatus.CONFLICT); //401
        } else {
            Player newPlayer = new Player(userName, password);
            playerRepository.save(newPlayer);
            return new ResponseEntity<>(makeMap("id", newPlayer.getId()), HttpStatus.CREATED);
        }
    }


    private Map<String, Object> makeMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }


    @RequestMapping(path = "/games", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> createNewGame(Authentication authentication) {

        Player currentPlayer = getCurrentPlayer(authentication);

        if (currentPlayer == null) {

            return new ResponseEntity<>(makeMap("Error", "No current Player"), HttpStatus.UNAUTHORIZED); //401

        } else {
            Game newGame = new Game();
            gameRepository.save(newGame);

            GamePlayer newGamePlayer = new GamePlayer(newGame, currentPlayer);
            gamePlayerRepository.save(newGamePlayer);

            return new ResponseEntity<>(makeMap("gpid", newGamePlayer.getId()), HttpStatus.CREATED);

        }

    }


    @RequestMapping(path = "/game/{gameID}/players", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> joinGame(@PathVariable Long gameID, Authentication authentication) {


        if (authentication == null)
            return new ResponseEntity<>(makeMap("Error", "Not authorised "), HttpStatus.UNAUTHORIZED); //401
        Game existingGame = gameRepository.findOne(gameID);


        if (existingGame == null) {
            return new ResponseEntity<>(makeMap("Error", "There is no game"), HttpStatus.FORBIDDEN); //403
        }

        Player userPlayer = getCurrentPlayer(authentication);

        if (userPlayer == null)
            return new ResponseEntity<>(makeMap("Error", "The player doesn't exist"), HttpStatus.UNAUTHORIZED); //401


        if (existingGame.getGamePlayers().size() == 2)
            return new ResponseEntity<>(makeMap("Error", "This Game is Already Full"), HttpStatus.FORBIDDEN);


        GamePlayer currentGamePlayerInGame = existingGame.getGamePlayers().stream().findFirst().orElse(null);
        Player currentPlayerInGame = currentGamePlayerInGame.getPlayer();

        if (userPlayer.getId() == currentPlayerInGame.getId())
            return new ResponseEntity<>(makeMap("Error", "You have already joined this game"), HttpStatus.FORBIDDEN);


        GamePlayer newGamePlayer = new GamePlayer(existingGame, userPlayer);
        gamePlayerRepository.save(newGamePlayer);
        return new ResponseEntity<>(makeMap("gamePlayerID", newGamePlayer.getId()), HttpStatus.CREATED);

    }


    @RequestMapping(path = "/games/players/{gamePlayerId}/ships", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addShip(@PathVariable Long gamePlayerId,
                                                       @RequestBody List<Ship> ships,
                                                       Authentication authentication) {



        if (ships.isEmpty())
            return new ResponseEntity<>(makeMap("Error", "You haven't placed any ships!"), HttpStatus.UNAUTHORIZED); //401
        if (authentication == null)
            return new ResponseEntity<>(makeMap("Error", "Please login first"), HttpStatus.UNAUTHORIZED); //401

        GamePlayer currentGamePlayerInGame = gamePlayerRepository.findOne(gamePlayerId);

        if (currentGamePlayerInGame == null)
            return new ResponseEntity<>(makeMap("Error", "The player doesn't exist"), HttpStatus.UNAUTHORIZED); //401

        Player userPlayer = getCurrentPlayer(authentication);
        Player currentPlayerInGame = currentGamePlayerInGame.getPlayer();

        if (userPlayer != currentPlayerInGame)
            return new ResponseEntity<>(makeMap("Error", "You can't do this"), HttpStatus.UNAUTHORIZED); //401
        if (!currentGamePlayerInGame.getShips().isEmpty())
            return new ResponseEntity<>(makeMap("Error", "There are no ships"), HttpStatus.UNAUTHORIZED); //401
        if (ships.size() != 5)
            return new ResponseEntity<>(makeMap("Error", "You can only place five ships per game"), HttpStatus.UNAUTHORIZED); //401
        if (checkForDuplicates(ships))
            return new ResponseEntity<>(makeMap("Error", "You can't put ships on top of each other"), HttpStatus.FORBIDDEN); //403


        for (Ship ship : ships) {
//            if (ship.getShipLocations().size() != 0)
            ship.setGamePlayer(currentGamePlayerInGame);
            shipRepository.save(ship);
        }



        return new ResponseEntity<>(makeMap("Success", "Ships created"), HttpStatus.CREATED); //200

    }


    @RequestMapping(path = "/games/players/{gamePlayerId}/salvos", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addSalvos(@PathVariable Long gamePlayerId,
                                                         @RequestBody Salvo salvo,
                                                         Authentication authentication) {

        if (salvo.getSalvoLocations().isEmpty())
            return new ResponseEntity<>(makeMap("Error", "You haven't fired any salvos"), HttpStatus.UNAUTHORIZED); //401

        if (authentication == null)
            return new ResponseEntity<>(makeMap("Error", "Please login first"), HttpStatus.UNAUTHORIZED); //401

        GamePlayer currentGamePlayerInGame = gamePlayerRepository.findOne(gamePlayerId);

        if (currentGamePlayerInGame == null)
            return new ResponseEntity<>(makeMap("Error", "There is no game player with the given ID"), HttpStatus.UNAUTHORIZED); //401

        Player userPlayer = getCurrentPlayer(authentication);
        Player currentPlayerInGame = currentGamePlayerInGame.getPlayer();

        if (userPlayer != currentPlayerInGame)
            return new ResponseEntity<>(makeMap("Error", "You are not the game player the current ID references"), HttpStatus.UNAUTHORIZED); //401


        salvo.setGamePlayer(currentGamePlayerInGame);
        salvo.setTurnNumber(findTurnNumber(currentGamePlayerInGame) + 1);
        salvoRepository.save(salvo);


        return new ResponseEntity<>(makeMap("Success", "Salvos have been fired"), HttpStatus.CREATED); //200

    }


    public Integer findTurnNumber(GamePlayer gamePlayer) {

        return gamePlayer.getSalvos().size();

    }


    public boolean checkForDuplicates(List<Ship> ships) {
        List<String> array = new ArrayList<>();
        boolean duplicates = false;
        for (Ship ship : ships) {
            for (String location : ship.getShipLocations()) {
                if (!array.contains(location)) {
                    array.add(location);
                } else {
                    duplicates = true;
                }
            }
        }

        return duplicates;


    }


    @RequestMapping(path = "/players", method = RequestMethod.GET)
    private List<Object> getPlayers() {

        return playerRepository.findAll()
                .stream()
                .map(player -> makePlayerDTO(player))
                .collect(Collectors.toList());

    }


    @RequestMapping(path = "/scoreboard", method = RequestMethod.GET)
    private List<Object> getScoreboard() {

        return playerRepository
                .findAll()
                .stream()
                .map(player -> makeScoreDTO(player))
                .collect(Collectors.toList());

    }


    @RequestMapping(path = "/gamePlayers")
    private List<Object> getAllGamePlayers() {
        return gamePlayerRepository
                .findAll()
                .stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(Collectors.toList());
    }


    @RequestMapping(path = "/game_view/{gamePlayerID}")
    private Map<String, Object> gameViewGenerator(@PathVariable Long gamePlayerID,
                                                  Authentication authentication) {

        Player currentPlayer = getCurrentPlayer(authentication);
        GamePlayer gamePlayer = gamePlayerRepository.findOne(gamePlayerID);
        Map<String, Object> object = new LinkedHashMap<>();

        if (currentPlayer.getId() == gamePlayer.getPlayer().getId()) {

            object.put("gameplayer_id", gamePlayer.getId());
            object.put("game", makeGameDTO(gamePlayer.getGame()));
            object.put("ships", gamePlayer.getShips()
                    .stream()
                    .map(ship -> makeShipDTO(ship, authentication))
                    .collect(Collectors.toList()));
            object.put("salvos", gamePlayer.getSalvos()
                    .stream()
                    .map(salvo -> makeSalvoDTO(salvo))
                    .collect(Collectors.toList()));
            object.put("host_salvo_locations", getSalvoList(gamePlayer));
            object.put("gamestate", getGameState(gamePlayer));
//            finalScore(gamePlayer);



            if (getOpponent(gamePlayer) != null) {
                setHits(gamePlayer);
                object.put("opponent_salvos", getOpponent(gamePlayer).getSalvos()
                        .stream()
                        .map(salvo -> makeSalvoDTO(salvo))
                        .collect(Collectors.toList()));

                object.put("opponent_ships", getOpponent(gamePlayer).getShips()
                        .stream()
                        .map(ship -> makeShipDTO(ship, authentication))
                        .collect(Collectors.toList()));
                object.put("hits_on_enemy", hitsLog(gamePlayer));
                object.put("hits_on_host", hitsLog(getOpponent(gamePlayer)));
                object.put("enemy_sunk_ships", shipsSunk(getOpponent(gamePlayer)));
                object.put("host_sunk_ships", shipsSunk(gamePlayer));
                finalScore(gamePlayer);

            }
        } else {
            object.put("Error:", "Access is forbidden");
        }
        return object;
    }


    private List<String> getSalvoList(GamePlayer gamePlayer) {

        List<String> list = new ArrayList<>();
        for (Salvo salvo : gamePlayer.getSalvos()) {
            list.addAll(salvo.getSalvoLocations());
        }

        return list;
    }


    private GamePlayer getOpponent(GamePlayer gamePlayer) {

        return gamePlayer.getGame().getGamePlayers()
                .stream()
                .filter(gp -> gp.getId() != gamePlayer.getId()).findFirst().orElse(null);
    }

    private Map<String, Object> makeSalvoDTO(Salvo salvo) {

        Map<String, Object> object = new LinkedHashMap<>();
        object.put("turn", salvo.getTurnNumber());
        object.put("player", salvo.getGamePlayer().getPlayer().getUserName());
        object.put("locations", salvo.getSalvoLocations());
        return object;

    }

    ;

    private Map<String, Object> makeShipDTO(Ship ship, Authentication authentication) {

        Map<String, Object> object = new LinkedHashMap<>();
        GamePlayer gamePlayer = ship.getGamePlayer();

        object.put("type", ship.getType());
        if (ship.getGamePlayer().getPlayer().getId() == playerRepository.findByUserName(authentication.getName()).getId())
            object.put("ship_locations", ship.getShipLocations());
        else
            object.put("hits_locations", ship.getHitLocations());

        return object;
    }


    private List<Map> hitsLog(GamePlayer gamePlayer) {


        List<Map> log = new ArrayList<>();

        for (Salvo salvo : gamePlayer.getSalvos()) {

            Map<String, Object> salvoMap = new HashMap<>();

            salvoMap.put("turn", salvo.getTurnNumber());
            List<Object> salvoHits = new ArrayList<>();
            for (Ship ship : getOpponent(gamePlayer).getShips()) {
                Map<String, Object> hittedShip = new HashMap<>();
                List<String> hitsOfTheSalvo = ship.getShipLocations()
                        .stream()
                        .filter(el -> salvo.getSalvoLocations().contains(el))
                        .collect(Collectors.toList());
                hittedShip.put("type", ship.getType());
                hittedShip.put("hits", hitsOfTheSalvo);
                if (hitsOfTheSalvo.size() > 0)
                    salvoHits.add(hittedShip);
            }
            salvoMap.put("ships", salvoHits);

            if (salvoHits.size() > 0)
                log.add(salvoMap);

        }

        return log;

    }


    @RequestMapping(path = "/games", method = RequestMethod.GET)
    private Map<String, Object> getGames(Authentication authentication) {

        Map<String, Object> object = new LinkedHashMap<>();


        object.put("current_player", makePlayerDTO(getCurrentPlayer(authentication)));
        object.put("games", gameRepository.findAll()
                .stream()
                .map(game -> makeGameDTO(game))
                .collect(Collectors.toList()));

        return object;

    }


    private Map<String, Object> makeGameDTO(Game game) {


        Map<String, Object> object = new LinkedHashMap<>();
        object.put("id", game.getId());
        object.put("created", game.getDate());
        object.put("players", game.getGamePlayers()
                .stream()
                .map(gamePlayer -> makeGamePlayerDTO(gamePlayer))
                .collect(Collectors.toList()));

        return object;
    }

    private Map<String, Object> makeGamePlayerDTO(GamePlayer gamePlayer) {
        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("gamePlayerID", gamePlayer.getId());
        dto.put("playerID", gamePlayer.getPlayer().getId());
        dto.put("username", gamePlayer.getPlayer().getUserName());
        if (gamePlayer.getScore() != null) {
            dto.put("score", gamePlayer.getScore().getScore());
        }

        return dto;
    }


    private Map<String, Object> makePlayerDTO(Player player) {
        Map<String, Object> object = new LinkedHashMap<>();
        object.put("id", player.getId());
        object.put("username", player.getUserName());
        return object;
    }


    private Map<String, Object> makeScoreDTO(Player player) {

        Map<String, Object> dto = new LinkedHashMap<>();
        dto.put("name", player.getUserName());
        double total = 0;
        int wins = 0;
        int losses = 0;
        int draws = 0;
        for (Score score : player.getScores()) {
            total += score.getScore();
            if (score.getScore() == 1.0)
                wins++;
            if (score.getScore() == 0.5)
                draws++;
            if (score.getScore() == 0.0)
                losses++;

        }

        dto.put("total_score", total);
        dto.put("number_wins", wins);
        dto.put("number_losses", losses);
        dto.put("number_ties", draws);

        return dto;


    }

    private List<String> getAllSalvosLocations(GamePlayer gamePlayer) {
        List<String> salvos = new ArrayList<>();
        for (Salvo salvo : gamePlayer.getSalvos()) {
            for (String salvoLocation : salvo.getSalvoLocations()) {
                salvos.add(salvoLocation);
            }
        }
        return salvos;
    }


    private void setHits(GamePlayer gamePlayer) {
        List<String> allSalvos = getAllSalvosLocations(getOpponent(gamePlayer));

        for (Ship ship : gamePlayer.getShips()) {
            List<String> hitsOnShip = new ArrayList<>();
            for (String location : ship.getShipLocations()) {
                if (allSalvos.contains(location))
                    hitsOnShip.add(location);
            }
            ship.setHitLocations(hitsOnShip);
            shipRepository.save(ship);
        }


    }


    private Map<String, Boolean> shipsSunk(GamePlayer gamePlayer) {


        Map<String, Boolean> shipsSunk = new LinkedHashMap<>();

        shipsSunk.put("carrierSunk", false);
        shipsSunk.put("battleshipSunk", false);
        shipsSunk.put("submarineSunk", false);
        shipsSunk.put("destroyerSunk", false);
        shipsSunk.put("patrolSunk", false);
        shipsSunk.put("allShipsSunk", false);


        for (Ship ship : gamePlayer.getShips()) {

            if ((ship.getType().equals("carrier")) && (ship.getHitLocations().size() == 5))
                shipsSunk.put("carrierSunk", true);

            if ((ship.getType().equals("battleship")) && (ship.getHitLocations().size() == 4))
                shipsSunk.put("battleshipSunk", true);

            if ((ship.getType().equals("submarine")) && (ship.getHitLocations().size() == 3))
                shipsSunk.put("submarineSunk", true);

            if ((ship.getType().equals("destroyer")) && (ship.getHitLocations().size() == 3))
                shipsSunk.put("destroyerSunk", true);

            if ((ship.getType().equals("patrol")) && (ship.getHitLocations().size() == 2))
                shipsSunk.put("patrolSunk", true);

        }

        if ((shipsSunk.get("carrierSunk").equals(true)) && (shipsSunk.get("battleshipSunk").equals(true)) && (shipsSunk.get("submarineSunk").equals(true)) && (shipsSunk.get("destroyerSunk").equals(true)) && (shipsSunk.get("patrolSunk").equals(true)))
            shipsSunk.put("allShipsSunk", true);

        return shipsSunk;


    }


    private String getGameState(GamePlayer gamePlayer) {


        if (gamePlayer.getShips().size() == 0)
            return "Place your Ships";

        GamePlayer oppGamePlayer = getOpponent(gamePlayer);

        if (oppGamePlayer == null)
            return "Wait for your opponent to join";
        if (oppGamePlayer != null){

            if ((gamePlayer.getSalvos().size() == oppGamePlayer.getSalvos().size()) && (shipsSunk(gamePlayer).get("allShipsSunk")) && (shipsSunk(oppGamePlayer).get("allShipsSunk")))
                return "Tie";

            if ((gamePlayer.getSalvos().size() == oppGamePlayer.getSalvos().size()) && (!shipsSunk(gamePlayer).get("allShipsSunk")) && (shipsSunk(oppGamePlayer).get("allShipsSunk")))
                return "Won";

            if ((gamePlayer.getSalvos().size() == oppGamePlayer.getSalvos().size()) && (shipsSunk(gamePlayer).get("allShipsSunk")) && (!shipsSunk(oppGamePlayer).get("allShipsSunk")))
                return "Lost";


            if ((gamePlayer.getSalvos().size() == oppGamePlayer.getSalvos().size()) && (gamePlayer.getId() > oppGamePlayer.getId()))
                return "Wait";

            if (gamePlayer.getSalvos().size() > oppGamePlayer.getSalvos().size())
                return "Wait";


            if ((gamePlayer.getSalvos().size() == oppGamePlayer.getSalvos().size()) && (gamePlayer.getId() < oppGamePlayer.getId()))
                return "Play";

            if (gamePlayer.getSalvos().size() < oppGamePlayer.getSalvos().size())
                return "Play";




        }

        return "Server Error";


    }


    private void finalScore (GamePlayer gamePlayer) {

        double finalScore = -1;

        Game game = gamePlayer.getGame();
        Player player = gamePlayer.getPlayer();

        if (getGameState(gamePlayer).equals("Won"))
            finalScore = 1.0;
        if (getGameState(gamePlayer).equals("Tie"))
            finalScore = 0.5;
        if (getGameState(gamePlayer).equals("Lost"))
            finalScore = 0.0;

        if (gamePlayer.getScore() == null && finalScore > -1) {

            Score score = new Score();

            score.setGame(game);
            score.setPlayer(player);
            score.setScore(finalScore);

            scoreRepository.save(score);
        }



    }











}









//    @RequestMapping(path = "/games", method = RequestMethod.GET)
//    private Map<String, Object> getGames(Authentication authentication) {
//
//        Map <String, Object> object = new LinkedHashMap<>();
//
//        object.put("current_player", makePlayerDTO(getCurrentPlayer(authentication)));
//        object.put("games", gameRepository.findAll()
//                .stream()
//                .map(game -> makeGameDTO(game))
//                .collect(Collectors.toList()));
//
//        return object;
//
//    }















