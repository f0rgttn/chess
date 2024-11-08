package main.piece;

import main.gamePanel;
import main.Type;

public class Rook extends Piece{

    public Rook(int color, int col, int row){
        super(color, col, row);

        type = Type.ROOK;

        if(color == gamePanel.WHITE){
            image = getImage("/piece/w-rook");
        }
        else{
            image = getImage("/piece/b-rook");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            if(targetCol == preCol || targetRow == preRow){
                if(isValidSquare(targetCol, targetRow) && pieceIsOnStraightLine(targetCol,targetRow) == false){
                    return true;
                }
            }
            
        }
        
        return false;
    }
}