package za.co.metalojiq.classfinder.someapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import za.co.metalojiq.classfinder.someapp.R;
import za.co.metalojiq.classfinder.someapp.model.network.Network;

import java.util.ArrayList;
import java.util.List;


public class MyNetworkTopicRecyclerViewAdapter extends RecyclerView.Adapter<MyNetworkTopicRecyclerViewAdapter.ViewHolder> {

    private final List<Network> mNetworks;
    private final OnListFragmentInteractionListener mListener;

    public MyNetworkTopicRecyclerViewAdapter(List<Network> items, OnListFragmentInteractionListener listener) {
        mNetworks = items;
        mListener = listener;
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

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mNetworks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mName;
        public final TextView mDesc;
        public Network mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mName = (TextView) view.findViewById(R.id.networks_name);
            mDesc = (TextView) view.findViewById(R.id.networks_desc);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mDesc.getText() + "'";
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Network item);
    }


    public void clear() {
        mNetworks.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Network> networks) {
        mNetworks.addAll(networks);
        notifyDataSetChanged();
    }



}
