package com.ivoafsilva.floowchallenge.ui;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ivoafsilva.floowchallenge.R;
import com.ivoafsilva.floowchallenge.model.Journey;

import java.util.List;

/**
 * The Adapter to use when showing journeys
 */
public class JourneyRecycleAdapter extends RecyclerView.Adapter<JourneyRecycleAdapter.JourneyViewHolder> {
    // ------------------------------------ VARIABLES -----------------------------------------

    /**
     * The Entities list
     */
    private List<? extends Journey> mJourneysList;

    /**
     * The callback for when an item is clicked
     */
    private final JourneyClickCallback mJourneyClickCallback;

    // ------------------------------------ METHODS -----------------------------------------

    public JourneyRecycleAdapter(JourneyClickCallback clickCallback) {
        mJourneyClickCallback = clickCallback;
    }

    public void setJourneyList(final List<? extends Journey> journeyList) {
        if (mJourneysList == null) {
            mJourneysList = journeyList;
            notifyItemRangeInserted(0, journeyList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mJourneysList.size();
                }

                @Override
                public int getNewListSize() {
                    return journeyList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mJourneysList.get(oldItemPosition).getId().equals(
                            journeyList.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Journey newJourney = journeyList.get(newItemPosition);
                    Journey oldJourney = mJourneysList.get(oldItemPosition);
                    return newJourney.getId().equals(oldJourney.getId())
                            && newJourney.getName().equals(oldJourney.getName())
                            && newJourney.getStartTime().equals(oldJourney.getStartTime())
                            && newJourney.getEndTime().equals(oldJourney.getEndTime());
                }
            });
            mJourneysList = journeyList;
            result.dispatchUpdatesTo(this);
        }
    }

    @Override
    public JourneyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View journey_item = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.journey_item, parent, false);
        return new JourneyViewHolder(journey_item);
    }

    @Override
    public void onBindViewHolder(JourneyViewHolder holder, int position) {
        holder.bindJourney(mJourneysList.get(position), mJourneyClickCallback);
    }

    @Override
    public int getItemCount() {
        return mJourneysList == null ? 0 : mJourneysList.size();
    }

    // ------------------------------------ STATIC CLASSES -----------------------------------------

    static class JourneyViewHolder extends RecyclerView.ViewHolder {

        TextView mName;

        JourneyViewHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.journey_name);
        }

        void bindJourney(final Journey journey, final JourneyClickCallback mJourneyClickCallback) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mJourneyClickCallback.onClick(journey);
                }
            });
            mName.setText(journey.getName());
        }
    }
}
