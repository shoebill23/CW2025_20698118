package com.comp2042;

public class GameController implements InputEventListener {

    private Board board = new SimpleBoard(25, 10);

    private final GuiController viewGuiController;

    public GameController(GuiController c) {
        viewGuiController = c;
        board.createNewBrick();
        viewGuiController.setEventListener(this);
        viewGuiController.initGameView(board.getBoardMatrix(), board.getViewData());
        viewGuiController.bindScore(board.getScore().scoreProperty());
        viewGuiController.updateHoldBrick(board.getHoldBrickData());
    }

    @Override
    public DownData onDownEvent(MoveEvent event) {
        boolean canMove = board.moveBrickDown();
        ClearRow clearRow = null;
        if (!canMove) {
            board.mergeBrickToBackground();
            clearRow = board.clearRows();
            if (clearRow.getLinesRemoved() > 0) {
                board.getScore().add(clearRow.getScoreBonus());
            }
            if (board.isGameOver()) {
                viewGuiController.gameOver();
            } else {
                board.createNewBrick();
                ((SimpleBoard) board).resetCanHold();
            }

            viewGuiController.refreshGameBackground(board.getBoardMatrix());

        } else {
            if (event.getEventSource() == EventSource.USER) {
                board.getScore().add(1);
            }
        }
        return new DownData(clearRow, board.getViewData());
    }

    @Override
    public ViewData onLeftEvent(MoveEvent event) {
        board.moveBrickLeft();
        return board.getViewData();
    }

    @Override
    public ViewData onRightEvent(MoveEvent event) {
        board.moveBrickRight();
        return board.getViewData();
    }

    @Override
    public ViewData onRotateEvent(MoveEvent event) {
        board.rotateLeftBrick();
        return board.getViewData();
    }


    @Override
    public ViewData onHoldEvent() {
        ViewData viewData = board.holdBrick();
        viewGuiController.updateHoldBrick(board.getHoldBrickData());
        return viewData;
    }

    @Override
    public void createNewGame() {
        board.newGame();
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        viewGuiController.updateHoldBrick(board.getHoldBrickData());
    }
    
    @Override
    public DownData onHardDropEvent() {
        
        int rowsDropped = board.hardDrop();
        
        
        if (rowsDropped > 0) {
            int hardDropScore = 2 * rowsDropped;
            board.getScore().add(hardDropScore);
        }
        
        
        board.mergeBrickToBackground();
        
        
        ClearRow clearRow = board.clearRows();
        if (clearRow.getLinesRemoved() > 0) {
            board.getScore().add(clearRow.getScoreBonus());
        }
        
        
        if (board.isGameOver()) {
            viewGuiController.gameOver();
        } else {
            board.createNewBrick();
            ((SimpleBoard) board).resetCanHold();
        }
        
        
        viewGuiController.refreshGameBackground(board.getBoardMatrix());
        
        
        return new DownData(clearRow, board.getViewData());
    }
    
    public Board getBoard() {
        return board;
    }
}
