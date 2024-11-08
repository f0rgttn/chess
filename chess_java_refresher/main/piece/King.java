package main.piece;

import main.gamePanel;
import main.Type;

public class King extends Piece{

    public King(int color, int col, int row){
        super(color, col, row);
        
        type = Type.KING;

        if(color == gamePanel.WHITE){
            image = getImage("/piece/w-king");
        }
        else{
            image = getImage("/piece/b-king");
        }
    }

    public boolean canMove(int targetCol, int targetRow){
       if(isWithinBoard(targetCol, targetRow)){
            //checks vertical and horizontal with addition, checks diaganol with multiplication
            if(Math.abs(targetCol - preCol) + Math.abs(targetRow - preRow) == 1 ||Math.abs(targetCol - preCol) * Math.abs(targetRow - preRow) == 1){
                if(isValidSquare(targetCol, targetRow)){
                    return true;
                }
            }

            //castling
            if(!moved){
                //short castke
                if(targetCol == preCol+2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)){
                    for(Piece piece : gamePanel.simPieces){
                        //check if unmoved rook 3 tiles away
                        if(piece.col == preCol+3 && piece.row == preRow && !piece.moved){
                            gamePanel.castlingP = piece;
                            return true;
                        }
                    }
                }
                //long castle
                if(targetCol == preCol-2 && targetRow == preRow && !pieceIsOnStraightLine(targetCol, targetRow)){
                    Piece p[] = new Piece[2];
                    for(Piece piece : gamePanel.simPieces){
                        if(piece.col == preCol - 3 && piece.row == targetRow){
                            p[0] = piece;
                        }
                        if(piece.col == preCol-4 && piece.row == targetRow){
                            p[1] = piece;
                        }
                        //p[1] needs to be unmoved rook, p[0] needs to be empty tile
                        if(p[0] == null && p[1] != null && !p[1].moved){
                            gamePanel.castlingP = p[1];
                            return true; 
                        }
                    }
                }
            }
       }
        return false;
    }
}
