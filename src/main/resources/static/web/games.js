var app = new Vue({
    el: "#vue-app",
    data: {
        games: [],
        usernames: [],
        scoreboard: [],
        username: "",
        password: "",
        show: false,
        user: [],
        alert: " ",
        host: null,
        hostGames: [],
        gamesList: [],
        joinGameData: [],
    },

    created: function () {
        this.getData();
        this.getScore();
    },

    computed: {



    },

    methods: {

        getData: function () {
            fetch("/api/games", {
                    method: "GET",
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                })
                .then(response => response.json())
                .then(json => {
                    data = json;
                    app.games = data.games;
                    app.user = data.current_player;
                    app.host = data.current_player.username;
                    app.createGamesList();
                    if (app.host != null)
                        app.show = true;



                })
                .catch(error => error)
        },

        getScore: function () {
            fetch("http://localhost:8080/api/scoreboard", {
                    method: "GET",
                    headers: {}
                })
                .then(response => response.json())
                .then(json => {
                    data = json;
                    app.scoreboard = data;
                    app.sortScore();
                })
                .catch(error => error)
        },

        login: function (username, password) {
            fetch("/api/login", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'userName=' + username + '&password=' + password,
                })
                .then(r => {
                    console.log(r)
                    if (r.status == 200) {
                        this.show = true
                        app.getData();
                    }

                })
                .catch(e => console.log(e))
        },

        logout: function () {
            fetch("/api/logout", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(r => {
                    console.log(r)
                    //if (r.status == 200) 
                })
                .catch(e => console.log(e));
            location.reload();


        },

        sortScore: function () {
            let score = app.scoreboard;
            score.sort(function (a, b) {
                return b.total_score - a.total_score;
            })
        },

        register: function (username, password) {
            fetch("/api/create-account", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                    body: 'userName=' + username + '&password=' + password,
                })
                .then(r => {
                    if (r.status == 200) {
                        app.show = true
                        app.login();
                    }
                    if (r.status == 403) {
                        app.alert = "Empty values, please enter a username and a password to register"
                    }
                    if (r.status == 409) {
                        app.alert = "The username:" + username + " already exists, please choose a different one"
                    }
                    console.log(r.status)

                })
                .catch(e => console.log(e))
        },

        createUsernameArray: function () {
            let usernames = [];
            let dates = {};
            let gamePlayers = [];
            let length = app.games.length;
            for (let i = 0; i < length; i++) {
                let javaDate = app.games[i].created
                let date = new Date(javaDate);
                date = date.toString();

                usernames.push({
                    game_id: app.games[i].id,
                    player_1: app.games[i].gamePlayers[0].player.username,
                    player_2: app.games[i].gamePlayers[1].player.username,
                    date: date
                });
            };
            app.usernames = usernames;
        },

        createGamesList: function () {
            let host = app.host;
            let hostGames = [];
            let length = app.games.length;


            for (let i = 0; i < length; i++) {

                let object = {};

                if (app.games[i].players.length == 1) {
                    let player1 = app.games[i].players[0].username;

                    if (host == player1) {
                        object = {
                            host: player1,
                            visitor: "Waiting for Opponent to Join",
                            game_id: app.games[i].id,
                            gameplayer_id: app.games[i].players[0].gamePlayerID
                        }

                        hostGames.push(object);

                    } else {
                        object = {
                            host: player1,
                            visitor: "Join Game",
                            game_id: app.games[i].id,
                            gameplayer_id: app.games[i].players[0].gamePlayerID
                        }

                        hostGames.push(object);
                    }


                } else {
                    let player1 = app.games[i].players[0].username;
                    let player2 = app.games[i].players[1].username;

                    if (host == player2) {
                        object = {
                            host: player2,
                            visitor: player1,
                            game_id: app.games[i].id,
                            gameplayer_id: app.games[i].players[1].gamePlayerID
                        }

                        hostGames.push(object);

                    }
                    if (host == player1) {
                        object = {
                            host: player1,
                            visitor: player2,
                            game_id: app.games[i].id,
                            gameplayer_id: app.games[i].players[0].gamePlayerID
                        }

                        hostGames.push(object);
                    }
                }

            }
            this.gamesList = hostGames;
        },

        //        GamesList: function () {
        //            let gamesList = [];
        //            let dates = {};
        //            let length = app.hostGames.length;
        //            
        //            for (let i = 0; i < length; i++) {
        //                let javaDate = app.hostGames[i].created
        //                let date = new Date(javaDate);
        //                date = date.toString();
        //                
        //                gamesList.push({
        //                    game_id: app.hostGames[i].id,
        //                    player_1: app.hostGames[i].players[0].username,
        //                    player_2: app.hostGames[i].players[1].username,
        //                    date: date
        //                });
        //            };
        //            this.gamesList = gamesList;
        //        },

        createGame: function () {
            fetch("/api/games", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(r => r.json())
                .then(json => window.open('/web/game.html?gp=' + json.gpid, "_blank"))
                .catch(e => console.log(e))

        },

        joinGame: function (gameID) {
            
            console.log(gameID);


            fetch("/api/game/" + gameID + "/players", {
                    credentials: 'include',
                    method: 'POST',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/x-www-form-urlencoded'
                    },
                })
                .then(r => r.json())
                .then(json => window.open('/web/game.html?gp=' + json.gamePlayerID, "_blank"))
                .catch(e => console.log(e))

        },

    }



//bind:href="'/web/game.html?gp=' + joinGameData.gamePlayerID" 

})
