package com.example.paige.perpetualmotion.lib;

import android.animation.Animator;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.paige.perpetualmotion.R;
import com.example.perpetual_motion.pm_game.Card;

import java.util.Locale;

/**
 * RecyclerView Adapter that displays checkable 4 cards and the number of cards beneath each one.
 */

public class CardPilesAdapter extends RecyclerView.Adapter<CardPilesAdapter.ViewHolder>
{
    // Listener class to pass click event and data back to Activity
    private static OIClickListener sOIClickListener;

    // Adapter Primary Data Source
    private final Card[] mPILE_TOPS;

    // Parallel arrays to primary source

    // which piles are checked
    private final boolean[] mCHECKED_PILES;

    // how many cards are underneath each pile
    private final int[] mNUMBER_OF_CARDS_IN_PILE;
    private final String mMSG_CARDS_IN_STACK;

    public void setOnItemClickListener (OIClickListener oiClickListener)
    {
        CardPilesAdapter.sOIClickListener = oiClickListener;
    }

    public CardPilesAdapter (Context context, int msgCardsInStack)
    {
        int NUMBER_OF_PILES = 4;
        mPILE_TOPS = new Card[NUMBER_OF_PILES];
        mCHECKED_PILES = new boolean[NUMBER_OF_PILES];
        mNUMBER_OF_CARDS_IN_PILE = new int[NUMBER_OF_PILES];
        mMSG_CARDS_IN_STACK = context.getString (msgCardsInStack);
    }

    @NonNull @Override public ViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType)
    {
        View itemLayoutView = LayoutInflater.from (parent.getContext ()).inflate (
                R.layout.rv_item_pile_top_card, parent, false);
        return new ViewHolder (itemLayoutView);
    }

    @Override public void onBindViewHolder (@NonNull ViewHolder holder, int position)
    {
        updateOuterCard (holder, position);
        updateInnerCard (holder, position);
    }

    private void updateOuterCard (ViewHolder holder, int position)
    {
        holder.tv_pile_card_cards_in_stack.setText (
                mMSG_CARDS_IN_STACK.concat (Integer.toString (mNUMBER_OF_CARDS_IN_PILE[position])));
    }

    private void updateInnerCard (ViewHolder holder, int position)
    {
        if (mPILE_TOPS[position] != null) {
            populateAndShowInnerCard (holder, position);
        }
        else {
            clearAndHideInnerCard (holder);
        }
    }

    private void populateAndShowInnerCard (ViewHolder holder, int position)
    {
        holder.tv_pile_card_rank_top.setTextColor (mPILE_TOPS[position].getSuit ().getColor ());
        holder.tv_pile_card_rank_top.setText (
                String.format (
                        Locale.getDefault (), "%d", mPILE_TOPS[position].getRank ().getValue ()));

        holder.tv_pile_card_suit_center.setTextColor (mPILE_TOPS[position].getSuit ().getColor ());
        holder.tv_pile_card_suit_center.setText (
                Character.toString (mPILE_TOPS[position].getSuit ().getCharacter ()));

        holder.tv_pile_card_name_bottom.setTextColor (mPILE_TOPS[position].getSuit ().getColor ());
        holder.tv_pile_card_name_bottom.setText (
                mPILE_TOPS[position].getRank ().toString ());

        holder.cb_pile_card_checkbox.setChecked (mCHECKED_PILES[position]);

        holder.cv_pile_inner_Card.setVisibility (View.VISIBLE);
    }

    private void clearAndHideInnerCard (ViewHolder holder)
    {
        holder.tv_pile_card_rank_top.setText("");
        holder.tv_pile_card_suit_center.setText("");
        holder.tv_pile_card_name_bottom.setText("");
        holder.cb_pile_card_checkbox.setChecked (false);

        holder.cv_pile_inner_Card.setVisibility (View.INVISIBLE);
    }

    @Override public int getItemCount ()
    {
        return mPILE_TOPS.length;
    }


    public void updatePile (int pileNumber, Card card, int numberOfCardsInStack)
    {
        mPILE_TOPS[pileNumber] = card;
        mNUMBER_OF_CARDS_IN_PILE[pileNumber] = numberOfCardsInStack;

        notifyItemChanged (pileNumber);
    }

    public Card getCardAt (int position)
    {
        return mPILE_TOPS[position] == null ? null : mPILE_TOPS[position].copy ();
    }

    public void clearCheck (int position)
    {
        if (mCHECKED_PILES[position]) {
            mCHECKED_PILES[position] = false;

            notifyItemChanged (position);
        }
    }

    public void toggleCheck (int position)
    {
        mCHECKED_PILES[position] = !mCHECKED_PILES[position];
        notifyItemChanged (position);
    }

    public void overwriteChecksFrom (boolean[] newChecksSet)
    {
        System.arraycopy (newChecksSet, 0, mCHECKED_PILES, 0, mCHECKED_PILES.length);
    }

    public boolean[] getCheckedPiles ()
    {
        return mCHECKED_PILES.clone ();
    }


    @Override public void onViewAttachedToWindow (@NonNull ViewHolder holder)
    {
        super.onViewAttachedToWindow (holder);
        animateCard (holder.cv_pile_inner_Card);
    }

    private void animateCard (View view)
    {
        if (Build.VERSION.SDK_INT >= 21) {
            int centerX = 0, centerY = 0, startRadius = 0;
            int endRadius = Math.max (view.getWidth (), view.getHeight ());
            Animator circularRevealAnimationOfCard = ViewAnimationUtils.createCircularReveal
                    (view, centerX, centerY, startRadius, endRadius);
            circularRevealAnimationOfCard.start ();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        public final CardView cv_pile_inner_Card;
        public final TextView tv_pile_card_rank_top, tv_pile_card_name_bottom,
                tv_pile_card_suit_center, tv_pile_card_cards_in_stack;
        public final CheckBox cb_pile_card_checkbox;

        public ViewHolder (View itemLayoutView)
        {
            super (itemLayoutView);

            cv_pile_inner_Card = itemLayoutView.findViewById (R.id.pile_card_inner_card);
            tv_pile_card_rank_top = itemLayoutView.findViewById (R.id.pile_card_rank_top);
            tv_pile_card_name_bottom = itemLayoutView.findViewById (R.id.pile_card_name_bottom);
            tv_pile_card_suit_center = itemLayoutView.findViewById (R.id.pile_card_suit_center);
            tv_pile_card_cards_in_stack = itemLayoutView.findViewById (R.id.pile_card_in_stack);
            cb_pile_card_checkbox = itemLayoutView.findViewById (R.id.pile_card_checkbox);

            cb_pile_card_checkbox.setClickable (false);

            itemLayoutView.setOnClickListener (this);
        }

        @Override public void onClick (View view)
        {
            sOIClickListener.onItemClick (getAdapterPosition (), view);
        }
    }

    // used to send data out of Adapter - implemented in the calling Activity/Fragment
    @SuppressWarnings ("UnusedParameters")
    public interface OIClickListener
    {
        void onItemClick (int position, View v);
    }
}
