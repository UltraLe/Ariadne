package it.icarramba.ariadne.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import it.icarramba.ariadne.R;
import it.icarramba.ariadne.entities.Itinerary;
import it.icarramba.ariadne.entities.ItineraryMonument;


//adapter that is used in the 'horizontal' recycler view,
//the one that lists the monuments in an itinerary
public class ItineraryAdapter extends RecyclerView.Adapter<ItineraryAdapter.SavedItinerariesViewHolder> {

    private Itinerary itinerary;

    public ItineraryAdapter(Itinerary itinerary){
        this.itinerary = itinerary;
    }

    public static class SavedItinerariesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        public ImageView picture;
        public TextView name;
        public TextView position;
        public TextView expectedArrTime;

        public SavedItinerariesViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.monumTitleItin);
            picture = v.findViewById(R.id.monImageItin);
            position = v.findViewById(R.id.monumPos);
            expectedArrTime = v.findViewById(R.id.expArrTime);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //when a monument is clicked, diplay a dialog with the monument_layout
            //TODO
        }
    }

    @NonNull
    @Override
    public SavedItinerariesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.monument_layout, parent, false);

        return new SavedItinerariesViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedItinerariesViewHolder holder, int position) {
        ItineraryMonument[] itiMon = itinerary.getItineraryMonuments();
        holder.name.setText(itiMon[position].getMonument().getName());
        holder.expectedArrTime.setText(itiMon[position].getExpectedArrTime());
        holder.position.setText(String.valueOf(itiMon[position].getPosition()));

        //if the monument has an image
        if(itiMon[position].getMonument().getPicture() != null){
            Bitmap bitmap = BitmapFactory.decodeByteArray(itiMon[position].getMonument().getPicture(),
                                        0, itiMon[position].getMonument().getPicture().length);
            holder.picture.setImageBitmap(bitmap);
        }else{
            //TODO something like image not found
        }

    }

    @Override
    public int getItemCount() {
        return itinerary.getItineraryMonuments() == null ? 0 : itinerary.getItineraryMonuments().length;
    }
}
