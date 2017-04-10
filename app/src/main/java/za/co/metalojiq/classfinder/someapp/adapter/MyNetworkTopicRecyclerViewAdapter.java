package za.co.metalojiq.classfinder.someapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.network.Network;
import za.co.metalojiq.classfinder.someapp.util.Utils;

import java.util.ArrayList;
import java.util.List;


public class MyNetworkTopicRecyclerViewAdapter extends RecyclerView.Adapter<MyNetworkTopicRecyclerViewAdapter.ViewHolder> {

    private final List<Network> mNetworks;
    private final OnListFragmentInteractionListener mListener;
    private final Context mContext;
    private ViewHolder mHolder;

    public enum CLICK_TYPE {VIEW, SHARE, SUBS_BUTTON}
    public MyNetworkTopicRecyclerViewAdapter(Context context, List<Network> items, OnListFragmentInteractionListener listener) {
        mNetworks = items;
        mListener = listener;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_network_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mNetworks.get(position);
        holder.mName.setText(mNetworks.get(position).getName());
        holder.mDesc.setText(mNetworks.get(position).getDescription());
        int userId = Utils.getUserId(mContext);

        if (userId ==  mNetworks.get(position).getCreatorId()) {
            holder.mShare.setVisibility(View.VISIBLE);
            holder.mSubscribe.setVisibility(View.GONE);
            holder.mShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onListFragmentInteraction(holder.mItem, CLICK_TYPE.SHARE);
                }
            });
        }

        holder.mSubscribe.setImageResource(mNetworks.get(position).isSubscribed() ? R.drawable.ic_done_black_24dp
                                                                                    : R.drawable.ic_not_subscribed_yet_black_24dp);
        mHolder = holder;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem, CLICK_TYPE.VIEW);
                }
            }
        });
        holder.mSubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onListFragmentInteraction(holder.mItem, CLICK_TYPE.SUBS_BUTTON);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mNetworks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mName;
        final TextView mDesc;
        final ImageView mSubscribe;
        final ImageView mShare;

        Network mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.networks_name);
            mDesc = (TextView) view.findViewById(R.id.networks_desc);
            mSubscribe = (ImageView) view.findViewById(R.id.subscribe);
            mShare = (ImageView) view.findViewById(R.id.shareNetwork);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDesc.getText() + "'";
        }
    }

    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Network item, CLICK_TYPE clickType);
    }


    public void clear() {
        mNetworks.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Network> networks) {
        mNetworks.addAll(networks);
        notifyDataSetChanged();
    }

    public void subscribe() {
        mHolder.mSubscribe.setImageResource(R.drawable.ic_done_black_24dp);
        notifyDataSetChanged();
    }

    public void unSubscribe() {
        mHolder.mSubscribe.setImageResource(R.drawable.ic_not_subscribed_yet_black_24dp);
        notifyDataSetChanged();
    }

    public void toggleSubscription() {
        mHolder.mSubscribe.setImageResource(mHolder.mItem.isSubscribed() ? R.drawable.ic_done_black_24dp
                : R.drawable.ic_not_subscribed_yet_black_24dp);
        notifyDataSetChanged();
    }




}
