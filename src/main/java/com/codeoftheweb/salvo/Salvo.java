package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.List;

@Entity
public class Salvo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer turnNumber;

    @ElementCollection
    @Column(name="salvoLocations")
    private List<String> salvoLocations;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Salvo(){};

    public Salvo(Integer turnNumber, List<String> salvoLocations, GamePlayer gamePlayer) {
        this.turnNumber = turnNumber;
        this.salvoLocations = salvoLocations;
        this.gamePlayer = gamePlayer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getTurnNumber() {
        return turnNumber;
    }

    public void setTurnNumber(Integer turnNumber) {
        this.turnNumber = turnNumber;
    }

    public List<String> getSalvoLocations() {
        return salvoLocations;
    }

    public void setSalvoLocations(List<String> salvoLocations) {
        this.salvoLocations = salvoLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }



}
