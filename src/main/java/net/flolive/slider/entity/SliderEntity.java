package net.flolive.slider.entity;

import javax.persistence.*;

@Entity
@Table(name = "TB_SLIDER")
public class SliderEntity {

    @Id
    private Integer id;

    @Column
    private int r;

    @Column
    private int g;

    @Column
    private int b;

    public SliderEntity(int id) {
        this.id = id;
    }

    public SliderEntity() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public int getG() {
        return g;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getB() {
        return b;
    }

    public void setB(int b) {
        this.b = b;
    }
}
