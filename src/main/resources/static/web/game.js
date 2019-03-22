    var app = new Vue({
        el: "#vue-app",
        data: {
            message: 'hello',
            numbers: ["1", "2", "3", "4", "5", "6", "7", "8", "9", "10"],
            letters: ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"],
            gameview: [],
            allData:[],
            shipLocations: [],
            colorCells: [],
            gameviewID: "",
            gamePlayers: [],
            greeting: "",
            host: "",
            opponent: "",
            salvos: [],
            hostGamePlayerID: "",
            opponentGamePlayerID: "",
            direction: 'Horizontal',
            shipPlacedLoc: [],
            shipType: "",
            shipLength: 0,
            shipClickedLoc: false,
            salvoHoverLoc: "",
            salvoFiredLoc: [],
            salvoSent: [{
                salvoLocations: [],
            }],
            allSalvos: [],
            selectSalvos: true,
            fireSalvos: true,
            enabled: false,
            hits: [],
            hitsLoc: [],
            hits_on_enemy: [],
            gamestate: "",
            enemy_sunk: [],
            host_sunk: [],
            hide_table: true,
            hide_loader: false,
            finale: null,
            fleet: [
                {
                    type: "carrier",
                    size: 5,
                    shipLocations: [null],
                },
                {
                    type: "battleship",
                    size: 4,
                    shipLocations: [null],
                },
                {
                    type: "submarine",
                    size: 3,
                    shipLocations: [null],
                },
                {
                    type: "destroyer",
                    size: 3,
                    shipLocations: [null],
                },
                {
                    type: "patrol",
                    size: 2,
                    shipLocations: [null],
                }
                ],

        },

        created() {

            this.setGameviewID();
            this.getData();


        },

        methods: {

            getData() {

                fetch("/api/game_view/" + this.gameviewID, {
                        method: "GET",
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    })
                    .then(response => response.json())
                    .then(json => {
                        data = json;
                        app.allData = data;
                        //                        app.gameview = data;
                        //                        app.shipLocations = data.ships;
                        //                        app.gamePlayers = data.game.players;
                        //                        app.allSalvos = data.host_salvo_locations;
                        //                        app.gamestate = app.gameview.gamestate;
                        //                        app.printShips();
                        //                        if (app.gamePlayers.length == 2) {
                        //                            app.hits = data.opponent_ships;
                        //                            app.hits_on_enemy = data.hits_on_enemy;
                        //                            app.printHits();
                        //                            app.printSalvos(data.salvos, "O", "hostFireshots");
                        //                            app.printSalvos(data.opponent_salvos, "U", "opponentFireshots");
                        //
                        //                        }

                        app.gameLogic(json);
                        app.createGreeting();
                        app.refreshPage();
                        app.fleetStatus();
//                        app.gameOver(json);




                    })
                    .catch(error => error)
            },
            
            getFinalGameState() {
                  fetch("/api/game_view/" + this.gameviewID, {
                        method: "GET",
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/x-www-form-urlencoded'
                        }
                    })
                    .then(response => response.json())
                    .then(json => app.finale = json.gamestate)
                    
            },


            gameLogic(data) {
                app.gameview = data;
                app.shipLocations = data.ships;
                app.gamePlayers = data.game.players;
                app.allSalvos = data.host_salvo_locations;
                app.printShips();
                if (app.gamePlayers.length == 2) {
                    app.hits = data.opponent_ships;
                    app.hits_on_enemy = data.hits_on_enemy;
                    app.printHits();
                    app.printSalvos(data.salvos, "O", "hostFireshots");
                    app.printSalvos(data.opponent_salvos, "U", "opponentFireshots");
                }
                app.gamestate = app.gameview.gamestate;

            },
            
            gameOver(data){
                
                    this.getFinalGameState();

                this.gamestate = "GameOver";
                    
            },


            printShips() {
                let tableHost = document.getElementById("table_one");
                for (let i = 0; i < this.shipLocations.length; i++) {
                    const ship = this.shipLocations[i];
                    let type = ship.type;
                    let typeLC = type.toLowerCase();
                    let locations = ship.ship_locations;
                    for (let j = 0; j < locations.length; j++) {
                        let cell = document.getElementById("U" + locations[j]);
                        cell.setAttribute("class", "ship-location " + typeLC);
                    }
                }
            },

            printSalvos(salvos, id, className) {

                for (let i = 0; i < salvos.length; i++) {

                    let turn = salvos[i].turn;
                    let locations = salvos[i].locations;
                    for (let j = 0; j < locations.length; j++) {
                        let cell = document.getElementById(id + locations[j]);
                        cell.classList.add(className);
                        cell.innerHTML = turn;
                    }
                }
            },

            printHits() {
                let hitsLoc = [];
                for (let i = 0; i < this.hits.length; i++) {
                    app.hits[i].hits_locations.forEach(function (el) {
                        hitsLoc.push(el);
                    })

                }

                app.hitsLoc = hitsLoc;

            },





            setGameviewID() {
                let url = window.location.href;
                let char = url.indexOf("=");
                let gameviewID = url.slice(char + 1);
                this.gameviewID = gameviewID;
            },


            createGreeting() {
                let length = this.gamePlayers.length;
                let greeting = " ";
                let host, opponent, hostGamePlayerID, opponentGamePlayerID;

                for (let i = 0; i < length; i++) {
                    if (this.gamePlayers[i].gamePlayerID == this.gameviewID) {

                        host = this.gamePlayers[i].username
                        hostGamePlayerID = this.gamePlayers[i].gamePlayerID
                    } else {
                        opponent = this.gamePlayers[i].username
                        opponentGamePlayerID = this.gamePlayers[i].gamePlayerID
                    }
                }

                if (opponent != "undefined") {
                    this.greeting = "Hi " + host + " you are playing against " + opponent;;
                } else {
                    this.greeting = "Hi " + host + " please wait for a player to join your game"
                }


                app.host = host;
                app.opponent = opponent;
                app.hostGamePlayerID = hostGamePlayerID;
                app.opponentGamePlayerID = opponentGamePlayerID;

            },


            addShips() {

                if (!app.fleet[0].shipLocations.includes(null) &&
                    !app.fleet[1].shipLocations.includes(null) && !app.fleet[2].shipLocations.includes(null) && !app.fleet[3].shipLocations.includes(null) && !app.fleet[4].shipLocations.includes(null)) {


                    fetch("/api/games/players/" + this.hostGamePlayerID + "/ships", {
                            credentials: 'include',
                            method: 'POST',
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json'
                            },
                            body: JSON.stringify(app.fleet)
                        })
                        .then(r => r.json())
                        .then(r => {
                            console.log(r)
                            if (r.status == 200) {

                            }
                            if (r.status == 403) {

                            }
                            app.getData();
                        })
                        .catch(e => console.log(e))

                } else {
                    alert("You need to place all five ships");


                }



            },




            shipSelector(shipType) {

                let dir = this.direction;
                let ship = shipType.toLowerCase();
                let length = this.fleet.length;
                let size = 0;

                for (let i = 0; i < length; i++) {
                    if (ship == this.fleet[i].type) {
                        size = this.fleet[i].size
                    }
                }



                this.shipType = ship;
                this.shipLength = size;

            },


            mouseHover(id) {
                let direction = this.direction;
                let letters = this.letters;
                let numbers = this.numbers;
                let shipLength = this.shipLength;
                let firstCellID = id;
                let locations = [];
                let char = firstCellID.charAt(1); // gives the letter i.e D
                let cellNumber = parseInt(firstCellID.split(char)[1]); // Array i.e ["D","9"] gives the last number

                let tempLocations = [];
                for (let i = 0; i < this.fleet.length; i++) {

                    if (this.fleet[i].shipLocations.length > 0) {
                        for (let j = 0; j < this.fleet[i].shipLocations.length; j++) {
                            tempLocations.push(this.fleet[i].shipLocations[j])
                        }
                    }

                }

                if (direction == "Horizontal") {

                    let firstCellIndexNumber = letters.indexOf(char);

                    for (let i = firstCellIndexNumber; i < firstCellIndexNumber + shipLength; i++) {
                        let location = null;
                        let letter = letters[i];

                        if (letter != null && !tempLocations.includes(letter + cellNumber))
                            location = letter + cellNumber;
                        locations.push(location);
                    }

                }
                if (direction == "Vertical") {


                    for (let i = cellNumber; i < cellNumber + shipLength; i++) {

                        let location = null;
                        let number = i;

                        if (number < 11 && !tempLocations.includes(char + number))
                            location = char + number;

                        locations.push(location);
                    }
                }



                this.shipPlacedLoc = locations;



            },



            mouseClick() {
                let shipType = this.shipType;
                let locations = this.shipPlacedLoc;
                let length = locations.length;
                if (!locations.includes(null)) {
                    for (let i = 0; i < this.fleet.length; i++) {
                        if (shipType == this.fleet[i].type) {
                            this.fleet[i].shipLocations = locations;
                        }
                    }

                }

                this.shipType = "";
                this.shipLength = 0;


            },

            setShipClass: function (letter, number) {
                let selectedClass;
                if (this.shipPlacedLoc.includes(letter + number))
                    selectedClass = "shipPlaced";
                if (this.shipPlacedLoc.includes(letter + number) && this.shipPlacedLoc.includes(null))
                    selectedClass = "exceeded";
                if (this.fleet[0].shipLocations.includes(letter + number))
                    selectedClass = "shipClickedLoc carrier";
                if (this.fleet[1].shipLocations.includes(letter + number))
                    selectedClass = "shipClickedLoc battleship";
                if (this.fleet[2].shipLocations.includes(letter + number))
                    selectedClass = "shipClickedLoc submarine";
                if (this.fleet[3].shipLocations.includes(letter + number))
                    selectedClass = "shipClickedLoc destroyer";
                if (this.fleet[4].shipLocations.includes(letter + number))
                    selectedClass = "shipClickedLoc patrol";

                return selectedClass
            },





            salvoHover(letter, number) {





                if (this.enabled === true && !this.allSalvos.includes(letter + number)) {
                    this.salvoHoverLoc = letter + number;
                }


            },




            salvoClick(letter, number) {


                if (this.enabled === true) {

                    if (!this.allSalvos.includes(letter + number)) {
                        let currentHovCell = letter + number;

                        if (this.salvoFiredLoc.length != 5) {
                            if (this.salvoFiredLoc.includes(currentHovCell)) {
                                let index = this.salvoFiredLoc.indexOf(currentHovCell);
                                this.salvoFiredLoc.splice(index, 1);
                            } else {
                                this.salvoFiredLoc.push(currentHovCell);
                            }

                        } else {

                            if (!this.salvoFiredLoc.includes(currentHovCell)) {
                                this.salvoFiredLoc.splice(0, 1);
                                this.salvoFiredLoc.push(currentHovCell);
                            } else {
                                let index = this.salvoFiredLoc.indexOf(currentHovCell);
                                this.salvoFiredLoc.splice(index, 1);
                            }
                        }
                    }
                } else {
                    alert("Please press the select button first");
                }




            },

            setSalvoClass(letter, number) {
                let salvoClass;


                if (this.salvoHoverLoc === letter + number)
                    salvoClass = "salvoFired"
                if (this.salvoFiredLoc.includes(letter + number))
                    salvoClass = "salvoReady"
                if (this.allSalvos.includes(letter + number))
                    salvoClass = "hostFireshots"
                if (this.hitsLoc.includes(letter + number))
                    salvoClass = "shipHit"

                return salvoClass;

            },


            salvoLeave(letter, number) {
                this.salvoHoverLoc = "";


            },





            addSalvos() {

                this.salvoSent[0].salvoLocations = this.salvoFiredLoc;



                fetch("/api/games/players/" + this.hostGamePlayerID + "/salvos", {
                        credentials: 'include',
                        method: 'POST',
                        headers: {
                            'Accept': 'application/json',
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify(app.salvoSent[0])
                    })
                    .then(r => r.json())
                    .then(r => {
                        console.log(r)
                        if (r.status == 200) {
                            app.salvoFiredLoc = [];
                        }
                        if (r.status == 401) {

                        }

                        app.getData();
                        this.enabled = false;
                    })
                    .catch(e => console.log(e))
            },


            startHover() {
                this.enabled = true;
            },


          
            refreshPage() {


                if ((this.gamestate == 'Wait') || (this.gamestate === 'Wait for your opponent to join'))

                {
                    setTimeout(function () {
                        app.getData()
                    }, 2000);

                }
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

            fleetStatus() {

                if (this.gameview.host_sunk_ships != "undefined") {
                    this.host_sunk = this.gameview.host_sunk_ships;
                }
                if (this.gameview.enemy_sunk_ships != "undefined") {
                    this.enemy_sunk = this.gameview.enemy_sunk_ships;
                }
            },




        },






    })



    //
    //        window.onload = function () {
    //        //canvas init
    //        var canvas = document.getElementById("canvas");
    //        var ctx = canvas.getContext("2d");
    //    
    //        //canvas dimensions
    //        var W = window.innerWidth;
    //        var H = window.innerHeight;
    //        canvas.width = W;
    //        canvas.height = H;
    //    
    //        //snowflake particles
    //        var mp = 200; //max particles
    //        var particles = [];
    //        for (var i = 0; i < mp; i++) {
    //            particles.push({
    //                x: Math.random() * W, //x-coordinate
    //                y: Math.random() * H, //y-coordinate
    //                r: Math.random() * 15 + 1, //radius
    //                d: Math.random() * mp, //density
    //                color: "rgba(" + Math.floor((Math.random() * 255)) + ", " + Math.floor((Math.random() * 255)) + ", " + Math.floor((Math.random() * 255)) + ", 0.8)",
    //                tilt: Math.floor(Math.random() * 5) - 5
    //            });
    //        }
    //    
    //        //Lets draw the flakes
    //        function draw() {
    //            ctx.clearRect(0, 0, W, H);
    //    
    //    
    //    
    //            for (var i = 0; i < mp; i++) {
    //                var p = particles[i];
    //                ctx.beginPath();
    //                ctx.lineWidth = p.r;
    //                ctx.strokeStyle = p.color; // Green path
    //                ctx.moveTo(p.x, p.y);
    //                ctx.lineTo(p.x + p.tilt + p.r / 2, p.y + p.tilt);
    //                ctx.stroke(); // Draw it
    //            }
    //    
    //            update();
    //        }
    //    
    //        //Function to move the snowflakes
    //        //angle will be an ongoing incremental flag. Sin and Cos functions will be applied to it to create vertical and horizontal movements of the flakes
    //        var angle = 0;
    //    
    //        function update() {
    //            angle += 0.01;
    //            for (var i = 0; i < mp; i++) {
    //                var p = particles[i];
    //                //Updating X and Y coordinates
    //                //We will add 1 to the cos function to prevent negative values which will lead flakes to move upwards
    //                //Every particle has its own density which can be used to make the downward movement different for each flake
    //                //Lets make it more random by adding in the radius
    //                p.y += Math.cos(angle + p.d) + 1 + p.r / 2;
    //                p.x += Math.sin(angle) * 2;
    //    
    //                //Sending flakes back from the top when it exits
    //                //Lets make it a bit more organic and let flakes enter from the left and right also.
    //                if (p.x > W + 5 || p.x < -5 || p.y > H) {
    //                    if (i % 3 > 0) //66.67% of the flakes
    //                    {
    //                        particles[i] = {
    //                            x: Math.random() * W,
    //                            y: -10,
    //                            r: p.r,
    //                            d: p.d,
    //                            color: p.color,
    //                            tilt: p.tilt
    //                        };
    //                    } else {
    //                        //If the flake is exitting from the right
    //                        if (Math.sin(angle) > 0) {
    //                            //Enter from the left
    //                            particles[i] = {
    //                                x: -5,
    //                                y: Math.random() * H,
    //                                r: p.r,
    //                                d: p.d,
    //                                color: p.color,
    //                                tilt: p.tilt
    //                            };
    //                        } else {
    //                            //Enter from the right
    //                            particles[i] = {
    //                                x: W + 5,
    //                                y: Math.random() * H,
    //                                r: p.r,
    //                                d: p.d,
    //                                color: p.color,
    //                                tilt: p.tilt
    //                            };
    //                        }
    //                    }
    //                }
    //            }
    //        }
    //    
    //        //animation loop
    //        setInterval(draw, 20);
    //    }
    //        
    //        
    //