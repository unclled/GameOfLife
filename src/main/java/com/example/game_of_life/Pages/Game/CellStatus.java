package com.example.game_of_life.Pages.Game;

public enum CellStatus {
    NONE,
    BORN,
    LIVE,
    DIED;

    public CellStatus prepareStep(int cellsAround) {
        switch (this) {
            case NONE:
                return (cellsAround == 3) ? CellStatus.BORN : CellStatus.NONE;
            case LIVE:
                return (cellsAround < 2 || cellsAround > 3) ? CellStatus.DIED : CellStatus.LIVE;
            default:
                return this;
        }
    }

    public CellStatus replaceStep() {
        switch (this) {
            case BORN: return LIVE;
            case DIED: return NONE;
            default: return this;
        }
    }

    public boolean isLive() {
        return this == LIVE || this == DIED;
    }
}
