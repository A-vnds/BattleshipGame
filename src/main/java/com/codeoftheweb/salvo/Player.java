package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String userName;
    private String password;

    public Player () {}

    public Player(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<GamePlayer> gamePlayers;


    public Set<GamePlayer> getGamePlayers() {
        return gamePlayers;
    }


    public void addGamePlayer(GamePlayer gamePlayer) {
        gamePlayer.setPlayer(this);
        gamePlayers.add(gamePlayer);
    }


    public void setGamePlayers(Set<GamePlayer> gamePlayers) {
        this.gamePlayers = gamePlayers;
    }


    @OneToMany(mappedBy="player", fetch=FetchType.EAGER)
    Set<Score> scores;

    public Set<Score> getScores() {
        return scores;
    }

    public void addScore(Score score){
        score.setPlayer(this);
        scores.add(score);
    }

    public void setScores(Set<Score> scores) {
        this.scores = scores;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
