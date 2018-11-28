package com.example.paige.perpetualmotion.lib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.paige.perpetualmotion.R;
import com.example.perpetual_motion.pm_game.Card;

public class CardPilesAdapter extends RecyclerView.Adapter<CardPilesAdapter.ViewHolder> {

    // Adapter main data source
    private final Card[] mPILE_TOPS;

    // which piles are checked
    private final boolean[] mCHECKED_PILES;

    //how many cards are in each pile, including the tops
    private final int[] mNUMBER_OF_CARDS_IN_FILE;

    //string reference from XML
    private final String mMSG_CARDS_IN_STACK;

    public CardPilesAdapter(Context context, int ID_MSG_CARDS_IN_STACK) {
        final int NUMBER_OF_PILES = 4;
        this.mPILE_TOPS = new Card[NUMBER_OF_PILES];
        this.mCHECKED_PILES = new boolean[NUMBER_OF_PILES];
        this.mNUMBER_OF_CARDS_IN_FILE = new int[NUMBER_OF_PILES];
        this.mMSG_CARDS_IN_STACK = context.getString(ID_MSG_CARDS_IN_STACK);
    }



    @NonNull
    @Override
    public CardPilesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext())
                        .inflate(R.layout
                        .rv_item_pile_top_card, viewGroup);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull CardPilesAdapter.ViewHolder viewHolder, int i) {
        updateOuterCard(viewHolder, i); // i = position
        updateInnerCard(viewHolder, i);

    }

    private void updateOuterCard(ViewHolder viewHolder, int i) {

    }

    private void updateInnerCard(ViewHolder viewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public final CardView cv_pile_inner_card;
        public final TextView tv_pile_card_rank_top;
        public final TextView tv_pile_card_rank_bottom;
        public final TextView tv_pile_card_suit_center;
        public final TextView tv_pile_card_cards_in_stack;

        public final CheckBox cb_pile_card_checkbox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cv_pile_inner_card = itemView.findViewById(R.id.pile_card_inner_card);
            tv_pile_card_rank_top = itemView.findViewById(R.id.pile_card_rank_top);
            tv_pile_card_rank_bottom = itemView.findViewById(R.id.pile_card_name_bottom);
            tv_pile_card_suit_center = itemView.findViewById(R.id.pile_card_suit_center);
            tv_pile_card_cards_in_stack = itemView.findViewById(R.id.pile_card_in_stack);
            cb_pile_card_checkbox = itemView.findViewById(R.id.pile_card_checkbox);
        }
    }
}
