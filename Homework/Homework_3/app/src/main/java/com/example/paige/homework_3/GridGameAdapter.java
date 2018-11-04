package com.example.paige.homework_3;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.Random;

public class GridGameAdapter extends RecyclerView.Adapter<GridGameAdapter.ViewHolder> {
    private boolean [] mSquares;
    private static int DEFAULT_ELEMENTS = 16;
    private int mElements, mWinningNumber;
    private Random mGenerator;

    public GridGameAdapter() {
        this(DEFAULT_ELEMENTS);
    }

    public GridGameAdapter(int elements){

       if(elements % Math.sqrt(elements) == 0){
           mSquares = new boolean[elements];
           mElements = elements;
           mGenerator = new Random();
           startGame();
       }
       else{
           throw new IllegalArgumentException
           ("Number must output a perfect square board");
       }
    }

    public boolean isWinner(int elementNumber) {
        return mSquares[elementNumber];
    }

    public int getWinningNumber() {
        return mWinningNumber;
    }

    private void endCurrentGame() {
        mSquares[mWinningNumber] = false;
    }

    private void startGame() {
        startGameWith(mGenerator.nextInt(mElements));
    }

    private void startGameWith(int winningNumber) {
        mWinningNumber = winningNumber;
        mSquares[mWinningNumber] = true;
    }

    public void startNewGame(){
        endCurrentGame();
        startGame();
    }


    public void overwriteWinningNumber(int newWinningNumber){
        if(newWinningNumber > 0 && newWinningNumber < mSquares.length){
            endCurrentGame();
            startGameWith(newWinningNumber);
        }
        else{
            throw new IllegalArgumentException("Invalid winning number.");
        }
    }

    @Override
    //will be called automatically to construct the individual square
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemLayoutView = LayoutInflater.from(viewGroup.getContext ())
                .inflate(R.layout.rv_item, viewGroup, false);
        return new ViewHolder (itemLayoutView);
    }

    @Override
    //will be called automatically after above method to bind this square to the data source
    public void onBindViewHolder(ViewHolder holder, int position) {
        String buttonNumber = String.valueOf(position);
        //need both otherwise it crashes
        holder.mButton.setTag(buttonNumber);
        holder.mButton.setText(buttonNumber);
    }

    @Override
    //call automatically to let the adapter know how many time it has
    public int getItemCount() {
        return mSquares.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Button mButton;
        public ViewHolder (View itemView) {
            super (itemView);
            mButton = itemView.findViewById (R.id.button);
        }
    }




}
