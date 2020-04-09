package it.icarramba.ariadne.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.icarramba.ariadne.R;
import it.icarramba.ariadne.entities.Itinerary;

public class AllItinerariesAdapter extends RecyclerView.Adapter<AllItinerariesAdapter.AllItinerariesVH>{

    private Itinerary[] itineraries;
    private Context context;
    private boolean searched;

    //TODO change in cursor adapter
    public AllItinerariesAdapter(Itinerary[] itineraries, Context context, boolean searched){
        this.itineraries = itineraries;
        this.context = context;
        this.searched = searched;
    }

    public static class AllItinerariesVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        public Button actionBtn;
        public RecyclerView rv;

        public AllItinerariesVH(@NonNull View itemView) {
            super(itemView);
            actionBtn = itemView.findViewById(R.id.actionBtn);
            rv = itemView.findViewById(R.id.rvMon);

            actionBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //TODO when clicked change btnText in deleted and delete from db
            //the itin saved
            if(v.getId() == R.id.actionBtn){
                //save itin into db
            }else if(v.getId() == R.id.actionBtn){
                //delete itin from db
            }
        }
    }

    @NonNull
    @Override
    public AllItinerariesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(searched){
            View v = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.itinerary_searched_layout, parent, false);
            return new AllItinerariesVH(v);
        }

        View v = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.itinerary_saved_layout, parent, false);

        return new AllItinerariesVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AllItinerariesVH holder, int position) {

        Itinerary itin = itineraries[position];
        ItineraryAdapter itiAdapter = new ItineraryAdapter(itin);
        holder.rv.setHasFixedSize(true);
        holder.rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv.setAdapter(itiAdapter);

    }

    @Override
    public int getItemCount() {
        return itineraries == null ? 0 : itineraries.length;
    }
}
