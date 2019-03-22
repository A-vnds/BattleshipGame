package com.codeoftheweb.salvo;


import javax.persistence.*;
import java.util.Date;

@Entity

public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Double score;
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="player_id")
    private Player player;

    public Score() {
    }

    public Score(Double score, Game game, Player player) {
        this.score = score;
        this.date = new Date();
        this.game = game;
        this.player = player;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
