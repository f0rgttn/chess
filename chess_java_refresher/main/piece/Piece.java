package main.piece;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import main.Board;
import main.Type;
import main.gamePanel;

public class Piece {

    public Type type;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;
    public Piece hittingP;
    public boolean moved, twoStepped;

    public Piece(int color, int col, int row){
        this.color = color;
        this.col = col;
        this.row = row;
        this.preCol = col;
        this.preRow = row;
        x = getX(col);
        y = getY(row);
    }

    public BufferedImage getImage(String imagePath){
        BufferedImage image = null;

        try{
            image = ImageIO.read(getClass().getResourceAsStream("/res/"+imagePath + ".png"));
        }catch(IOException e){
            e.printStackTrace();
        }
        return image;
    }

    public int getX(int col){
        return col * Board.SQUARE_SIZE;
    }
    public int getY(int row){
        return row * Board.SQUARE_SIZE;
    }
    public int getCol(int x){
        return (x + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }
    public int getRow(int y){
        return (y + Board.HALF_SQUARE_SIZE)/Board.SQUARE_SIZE;
    }
    public int getIndex(){
        for(int i = 0; i < gamePanel.simPieces.size(); i++)
        {
            if(gamePanel.simPieces.get(i) == this){
                return i;
            }
        }
        return -1;
    }

    public void updatePosition(){

        //check En Passant
        if(type == type.PAWN){
            if(Math.abs(row - preRow) == 2){
                twoStepped = true;
            }
        }

        x = getX(col);
        y = getY(row);
        preCol = getCol(x);
        preRow = getRow(y);
        moved = true;
    }
    public void resetPosition(){
        col = preCol;
        row = preRow;
        x = getX(col);
        y = getY(row);
    }

    //movement functions
    public boolean canMove(int targetCol, int targetRow){
        return false;
    }
    public boolean isWithinBoard(int targetCol, int targetRow){
        if(targetCol >= 0 && targetCol <= 7 && targetRow >=0 && targetRow <= 7){
            return true;
        }
        return false;
    }
    public boolean isSameSquare(int targetCol, int targetRow){
        if(targetCol == preCol && targetRow == preRow){
            return true;
        }
        return false;
    }
    public Piece getHittingP(int targetCol, int targetRow){
        for(Piece piece : gamePanel.simPieces){
            if(piece.col == targetCol && piece.row == targetRow && piece != this){
                return piece;
            }
        }
        return null;
    }
    public boolean isValidSquare(int targetCol, int targetRow) {
        hittingP = getHittingP(targetCol, targetRow);
        if (hittingP == null) { // Empty square
            return true;
        } else { // Occupied
            if (hittingP.color != this.color) {
                return true;
            } else {
                hittingP = null;
            }
        }
        return false; // Add this to return false if square is invalid
    }
    public boolean pieceIsOnStraightLine(int targetCol, int targetRow) {
        // Move left (decreasing columns)
        for (int i = preCol - 1; i > targetCol; i--) {
            for (Piece piece : gamePanel.simPieces) {
                if (piece.col == i && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // Move right (increasing columns)
        for (int i = preCol + 1; i < targetCol; i++) {
            for (Piece piece : gamePanel.simPieces) {
                if (piece.col == i && piece.row == targetRow) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // Move up (decreasing rows)
        for (int j = preRow - 1; j > targetRow; j--) {
            for (Piece piece : gamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == j) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        // Move down (increasing rows)
        for (int j = preRow + 1; j < targetRow; j++) {
            for (Piece piece : gamePanel.simPieces) {
                if (piece.col == targetCol && piece.row == j) {
                    hittingP = piece;
                    return true;
                }
            }
        }
        return false;
    }
    public boolean pieceIsOnDiagonalLine(int targetCol, int targetRow){
        
        if(targetRow < preRow){
            //up left
            for(int i = preCol-1; i > targetCol; i--){
                int diff = Math.abs(i - preCol);
                for(Piece piece : gamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
            //up right
            for(int i = preCol+1; i < targetCol; i++){
                int diff = Math.abs(i - preCol);
                for(Piece piece : gamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }
        if(targetRow > preRow){
            //down left
            for(int i = preCol+1; i > targetCol; i--){
                int diff = Math.abs(i - preCol);
                for(Piece piece : gamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow + diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
            //down right
            for(int i = preCol+1; i < targetCol; i++){
                int diff = Math.abs(i - preCol);
                for(Piece piece : gamePanel.simPieces){
                    if(piece.col == i && piece.row == preRow - diff){
                        hittingP = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }
    

    //draw piece function
    public void draw(Graphics2D g2){
        g2.drawImage(image,x, y, Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
    }
}

