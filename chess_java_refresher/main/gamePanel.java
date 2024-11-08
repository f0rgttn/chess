package main;
import javax.swing.JPanel;

import main.piece.Piece;
import main.piece.Bishop;
import main.piece.King;
import main.piece.Knight;
import main.piece.Pawn;
import main.piece.Queen;
import main.piece.Rook;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

public class gamePanel extends JPanel implements Runnable{

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int FPS = 60;
    Thread gameThread;  //too use need to implement runnable
    Board board = new Board();
    Mouse mouse = new Mouse();

    //Pieces
    public static ArrayList<Piece> pieces = new ArrayList<>();
    public static ArrayList<Piece> simPieces = new ArrayList<>();
    ArrayList<Piece> promoPieces = new ArrayList<>();
    Piece activeP, checkingP;
    public static Piece castlingP;

    //Colour
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    //bool
    boolean canMove;
    boolean validSquare;
    boolean promotion;
    boolean gameOver;

    
    public gamePanel() {
        setPreferredSize(new Dimension(WIDTH,HEIGHT));
        setBackground(Color.black);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    //run method creates a game loop
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
    }
    public void setPieces(){
        //white team;
        pieces.add(new Pawn(WHITE, 0, 6));
        pieces.add(new Pawn(WHITE, 1, 6));
        pieces.add(new Pawn(WHITE, 2, 6));
        pieces.add(new Pawn(WHITE, 3, 6));
        pieces.add(new Pawn(WHITE, 4, 6));
        pieces.add(new Pawn(WHITE, 5, 6));
        pieces.add(new Pawn(WHITE, 6, 6));
        pieces.add(new Pawn(WHITE, 7, 6));
        pieces.add(new Rook(WHITE, 0, 7));
        pieces.add(new Rook(WHITE, 7, 7));
        pieces.add(new Knight(WHITE, 1, 7));
        pieces.add(new Knight(WHITE, 6, 7));
        pieces.add(new Bishop(WHITE, 2, 7));
        pieces.add(new Bishop(WHITE, 5, 7));
        pieces.add(new Queen(WHITE, 3, 7));
        pieces.add(new King(WHITE, 4, 7));

        //black team
        pieces.add(new Pawn(BLACK, 0, 1));
        pieces.add(new Pawn(BLACK, 1, 1));
        pieces.add(new Pawn(BLACK, 2, 1));
        pieces.add(new Pawn(BLACK, 3, 1));
        pieces.add(new Pawn(BLACK, 4, 1));
        pieces.add(new Pawn(BLACK, 5, 1));
        pieces.add(new Pawn(BLACK, 6, 1));
        pieces.add(new Pawn(BLACK, 7, 1));
        pieces.add(new Rook(BLACK, 0, 0));
        pieces.add(new Rook(BLACK, 7, 0));
        pieces.add(new Knight(BLACK, 1, 0));
        pieces.add(new Knight(BLACK, 6, 0));
        pieces.add(new Bishop(BLACK, 2, 0));
        pieces.add(new Bishop(BLACK, 5, 0));
        pieces.add(new Queen(BLACK, 3, 0));
        pieces.add(new King(BLACK, 4, 0));
    }
    private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target){
        target.clear();
        for(int i = 0; i < source.size(); i++){
            target.add(source.get(i));
        }
    }

    @Override
    public void run(){

        //Game loop
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    //call next two methods 60 times a secondd
    private void update(){
        //check if promotion boolean is true
        if(promotion){
            promoting();
        }
        else{

            //mouse button pressed
        if(mouse.pressed){
            if(activeP == null){//chjeck if piece can be picked up
                for(Piece piece : simPieces){//if mouse on ally piece, pick up
                    if(piece.color == currentColor &&
                    piece.col == mouse.x/Board.SQUARE_SIZE &&
                    piece.row == mouse.y/Board.SQUARE_SIZE) {
                        activeP = piece;
                    }
                }
                
            }
            else{
                simulate();
            }
        }
        //MB release
        if(mouse.pressed == false){
            if(activeP != null){
                if(validSquare){ //move has been confirmed
                    copyPieces(simPieces, pieces);
                    activeP.updatePosition();
                    if(castlingP != null){
                        castlingP.updatePosition();
                    }

                    if(isKingInCheck()){
                        //TODO: possible game over
                    }
                    //else{
                        if(canPromote()){
                            promotion = true;
                        }
                        else{
                            changePlayer();
                        }
                    //}
                }
                else{
                    //the move is not valid so reset
                    copyPieces(pieces, simPieces);
                    activeP.resetPosition();
                    activeP = null;
                }
            }
        }
        }
        
    }
    private void simulate(){
        canMove = false;
        validSquare = false;

        //reset piece list in every loop
        copyPieces(pieces, simPieces);
        
        //reset castling piece's position
        if(castlingP != null){
            castlingP.col = castlingP.preCol;
            castlingP.x = castlingP.getX(castlingP.col);
            castlingP = null;
        }

        //if holding a piece, uppdate pos
        activeP.x = mouse.x - Board.HALF_SQUARE_SIZE;
        activeP.y = mouse.y - Board.HALF_SQUARE_SIZE;
        activeP.col = activeP.getCol(activeP.x);
        activeP.row = activeP.getRow(activeP.y);

        //Check if the piece is hovering over a reachable square
        if(activeP.canMove(activeP.col, activeP.row)){
            canMove = true;

            //if hitting a piece, remove it 
            if(activeP.hittingP != null){
                simPieces.remove(activeP.hittingP.getIndex());
            }

            checkCastling();

            if(!isIllegal(activeP) && !opponentCanCaptureKing()){
                validSquare = true;
            }
        }
    }
    private boolean isIllegal(Piece king){
        if(king.type == Type.KING){ 
            for(Piece piece : simPieces){ //checks if piece of opposing colour can reach kings desired spot 
                if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)){
                    return true;
                }
            }
        }
        return false;
    }
    private boolean opponentCanCaptureKing(){
        Piece king = getKing(false); //get current colour king (false)

        for(Piece piece : simPieces){
            if(piece.color != king.color && piece.canMove(king.col, king.row)){
                return true;
            }
        }
        return false;
    }
    private boolean isKingInCheck(){
        
        Piece king = getKing(true); 

        if(activeP.canMove(king.col, king.row)){
            checkingP = activeP;
            return true;
        }
        else {
            checkingP = null;
        }

        return false;
    }
    private Piece getKing(boolean opponent){
        Piece king = null;
        for(Piece piece : simPieces){
            if(opponent){
                if(piece.type == Type.KING && piece.color != currentColor){ //check if colour is not current colour
                    king = piece;
                }
            }
            else{
                if(piece.type == Type.KING && piece.color == currentColor){
                    king = piece;
                }
            }
        }
        return king;
    }
    private void checkCastling(){

        if(castlingP != null){
            if(castlingP.col == 0){ //long castle
                castlingP.col += 3;
            }
            else if(castlingP.col == 7){ //short castle
                castlingP.col -= 2;
            }
            castlingP.x = castlingP.getX(castlingP.col);
        }
    }
    private void changePlayer(){
        if(currentColor == WHITE){
            currentColor = BLACK;
            //reset blacks two steppped status
            for(Piece piece : pieces){
                if(piece.color == BLACK){
                    piece.twoStepped = false;
                }
            }
        }
        else{
            currentColor = WHITE;
            //reset whites two steppped status
            for(Piece piece : pieces){
                if(piece.color == WHITE){
                    piece.twoStepped = false;
                }
            }
        }
        activeP = null;
    }
    private boolean canPromote(){
        
        if(activeP.type == Type.PAWN){
            if(currentColor == WHITE && activeP.row == 0 || currentColor == BLACK && activeP.row == 7){
                promoPieces.clear();
                promoPieces.add(new Rook(currentColor, 9, 2));
                promoPieces.add(new Knight(currentColor, 9, 3));
                promoPieces.add(new Bishop(currentColor, 9, 4));
                promoPieces.add(new Queen(currentColor, 9, 5));
                return true;
            }
        }
        
        return false;
    }
    private void promoting(){
        if (mouse.pressed){ 
            for(Piece piece : promoPieces){
                if(piece.col == mouse.x/Board.SQUARE_SIZE && piece.row == mouse.y/Board.SQUARE_SIZE){
                    switch(piece.type){
                        case ROOK : simPieces.add(new Rook(currentColor,activeP.col, activeP.row)); break;
                        case KNIGHT : simPieces.add(new Knight(currentColor,activeP.col, activeP.row)); break;
                        case BISHOP : simPieces.add(new Bishop(currentColor,activeP.col, activeP.row)); break;
                        case QUEEN : simPieces.add(new Queen(currentColor,activeP.col, activeP.row)); break;
                        default: break;
                    }
                    simPieces.remove(activeP.getIndex());
                    copyPieces(simPieces, pieces);
                    activeP = null;
                    promotion = false;
                    changePlayer();
                }
            }
        }
    }

    //handles the drawing - e.g. drawing chess board/pieces
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        board.draw(g2);

        //pieces
        for(Piece p : simPieces){
            p.draw(g2);
        }

        if(activeP != null){
            if(canMove){
                if(isIllegal(activeP) || opponentCanCaptureKing()){
                    g2.setColor(Color.red);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE,activeP.row*Board.SQUARE_SIZE, 
                    Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));   
                }
                else{
                    g2.setColor(Color.white);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                    g2.fillRect(activeP.col*Board.SQUARE_SIZE,activeP.row*Board.SQUARE_SIZE, 
                    Board.SQUARE_SIZE, Board.SQUARE_SIZE);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));   
                }  
            }

            //Draw active piece in emd so it won't be hiddden by the board or the square 
            activeP.draw(g2);
        }

        //status message
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(new Font("Book Antiqua", Font.PLAIN, 40));
        g2.setColor(Color.white);

        if(promotion){
            g2.drawString("Promote to:", 840, 150);
            for(Piece piece : promoPieces){
                g2.drawImage(piece.image, piece.getX(piece.col), piece.getY(piece.row), Board.SQUARE_SIZE, Board.SQUARE_SIZE, null);
            } 
        }
        else {
            if(currentColor == WHITE){
                g2.drawString("White's turn", 840, 550);
                if(checkingP != null && checkingP.color == BLACK){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 650);
                    g2.drawString("is in check", 840, 700);
                }
            } 
            else {
                g2.drawString("Black's turn", 840, 250);
                if(checkingP != null && checkingP.color == WHITE){
                    g2.setColor(Color.red);
                    g2.drawString("The King", 840, 100);
                    g2.drawString("is in check", 840, 150);
                }
            }
        }


    }

}

