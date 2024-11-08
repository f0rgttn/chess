package main.piece;

import main.Type;
import main.gamePanel;

public class Pawn extends Piece{

    public Pawn(int color, int col, int row){
        super(color, col, row);

        type = Type.PAWN;

        if(color == gamePanel.WHITE){
            image = getImage("/piece/w-pawn");
        }
        else{
            image = getImage("/piece/b-pawn");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
        if(isWithinBoard(targetCol, targetRow) && !isSameSquare(targetCol, targetRow)){
            //defining move value based on colour
            int moveValue;

            if(color == gamePanel.WHITE){
                moveValue = -1;
            }
            else{
                moveValue = 1;
            }

            //check if hitting a piece, cannot use validSquare method with pawns
            hittingP = getHittingP(targetCol, targetRow);

            //1 square for move
            if(targetCol == preCol && targetRow == preRow + moveValue && hittingP == null){
                return true;
            }

            //2 square move
            if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingP == null && !moved && !pieceIsOnStraightLine(targetCol, targetRow)){
                return true;
            }

            //diagonal movement
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingP != null && hittingP.color != color){
                return true;
            }

            //en passant
            if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue){
                for(Piece piece : gamePanel.simPieces){
                    if(piece.col == targetCol && piece.row == preRow && piece.twoStepped == true)
                    {
                        hittingP = piece;
                        return true;
                    }
                }
            }


        }
        return false;
    }
}
