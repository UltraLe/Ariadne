package it.icarramba.ariadne.adapters;

import android.content.Context;
import android.database.SQLException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import it.icarramba.ariadne.R;
import it.icarramba.ariadne.constants.Constants;
import it.icarramba.ariadne.control.DBManager;
import it.icarramba.ariadne.entities.Itinerary;

public class AllItinerariesAdapter extends RecyclerView.Adapter<AllItinerariesAdapter.AllItinerariesVH>{

    private Itinerary[] itineraries;
    private Context context;
    private boolean searched;

    public AllItinerariesAdapter(Itinerary[] itineraries, Context context, boolean searched){
        this.itineraries = itineraries;
        this.context = context;
        this.searched = searched;
    }

    public static class AllItinerariesVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        public Button actionBtn;
        public RecyclerView rv;
        public Itinerary viewItinerary;

        public AllItinerariesVH(@NonNull View itemView) {
            super(itemView);
            actionBtn = itemView.findViewById(R.id.actionBtn);
            rv = itemView.findViewById(R.id.rvMon);

            actionBtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            //the itin saved
            if(v.getId() == R.id.actionBtn &&
                        ((Button)v).getText().toString().equals(v.getContext().getString(R.string.save)) ){
                //save itin into db
                viewItinerary.setType(Constants.ItineraryType_Saved);
                try {
                    DBManager.getInstance(v.getContext()).updateItineraryType(
                                            Constants.ItineraryType_Saved, viewItinerary.getID());
                }catch(SQLException e){
                    Toast.makeText(v.getContext(), R.string.db_exception, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                ((Button)v).setText(R.string.saved);
                Toast.makeText(v.getContext(), R.string.itin_saved, Toast.LENGTH_SHORT).show();

            }else if(v.getId() == R.id.actionBtn &&
                    ((Button)v).getText().toString().equals(v.getContext().getString(R.string.deleate)) ){

                try {
                    DBManager.getInstance(v.getContext()).deleteItinerary(viewItinerary);
                }catch(SQLException e){
                    Toast.makeText(v.getContext(), R.string.db_exception, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
                ((Button)v).setText(R.string.deleated);
                Toast.makeText(v.getContext(), R.string.itin_deleted, Toast.LENGTH_SHORT).show();

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
        ItineraryAdapter itiAdapter = new ItineraryAdapter(itin, context);
        holder.rv.setHasFixedSize(true);
        holder.rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        holder.rv.setAdapter(itiAdapter);
        holder.viewItinerary = itin;

    }

    @Override
    public int getItemCount() {
        return itineraries == null ? 0 : itineraries.length;
    }
}
