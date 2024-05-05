package com.example.game_of_life.Pages.Game;

@FunctionalInterface
public interface NeighborCountFunction {
    byte[] countAliveNeighbors();
}
