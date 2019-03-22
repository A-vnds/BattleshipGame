package com.codeoftheweb.salvo;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Ship {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;

    @ElementCollection
    @Column(name="shipLocations")
    private List<String> shipLocations;

    @ElementCollection
    @Column(name="hitsLocations")
    private List<String> hitLocations = new ArrayList<>();


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="gamePlayer_id")
    private GamePlayer gamePlayer;

    public Ship () {}

    public Ship(String type, List<String> shipLocations, GamePlayer gamePlayer) {
        this.type = type;
        this.shipLocations = shipLocations;
        this.gamePlayer = gamePlayer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getShipLocations() {
        return shipLocations;
    }

    public void setShipLocations(List shipLocations) {
        this.shipLocations = shipLocations;
    }

    public GamePlayer getGamePlayer() {
        return gamePlayer;
    }

    public void setGamePlayer(GamePlayer gamePlayer) {
        this.gamePlayer = gamePlayer;
    }

    public List<String> getHitLocations() {
        return hitLocations;
    }

    public void setHitLocations(List<String> hitLocations) {
        this.hitLocations = hitLocations;
    }
}
