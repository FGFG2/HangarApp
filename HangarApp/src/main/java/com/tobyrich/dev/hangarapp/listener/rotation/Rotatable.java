package com.tobyrich.dev.hangarapp.listener.rotation;

/**
 * Represents an entity that can be rotated along three axes.
 * Created by Jonas on 02.06.2015.
 */
public interface Rotatable {

    /**
     * Sets the deviation from the origin position of the Rotatable.
     * @param x - the x deviation
     * @param y - the y deviation
     * @param z - the z deviation
     */
    void setOriginDeviation(float x, float y, float z);
}
