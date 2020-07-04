package it.icarramba.ariadne.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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

    public Itinerary itinerary;
    private Context context;

    public ItineraryAdapter(Itinerary itinerary, Context context){
        this.itinerary = itinerary;
        this.context = context;
    }

    public static class SavedItinerariesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // each data item is just a string in this case
        public ImageView picture;
        public TextView name;
        public TextView position;
        public TextView expectedArrTime;

        //used to make easier the Dialog visualization
        public String monumDescription;

        public SavedItinerariesViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.expMonTitle);
            picture = v.findViewById(R.id.monImageItin);
            position = v.findViewById(R.id.monumPos);
            expectedArrTime = v.findViewById(R.id.expArrTime);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //when a monument is clicked, display a dialog with the monument_layout
            AlertDialog dialog;

            View monumentDialogView = LayoutInflater.from(v.getContext()).inflate
                    (R.layout.expanded_monument_layout, null);

            dialog = new AlertDialog.Builder(v.getContext())
                    .setView(monumentDialogView)
                    .setCancelable(true).create();

            ((TextView)monumentDialogView.findViewById(R.id.expMonTitle)).setText(((TextView)v.findViewById(R.id.expMonTitle)).getText());
            ((ImageView)monumentDialogView.findViewById(R.id.monImageItin)).setImageBitmap(((ImageView)v.findViewById(R.id.monImageItin)).getDrawingCache());
            ((TextView)monumentDialogView.findViewById(R.id.monumDescription)).setText(monumDescription);

            dialog.show();
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

        if(itiMon[position].getMonument().getDescription() == null){

            holder.monumDescription = context.getString(R.string.descri_not_provided);

        }else{
            holder.monumDescription = itiMon[position].getMonument().getDescription();
        }


        //if the monument has an image
        if(itiMon[position].getMonument().getPicture().length() > 0){
            byte[] decodedImage = Base64.decode(itiMon[position].getMonument().getPicture(), Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedImage,
                                        0, decodedImage.length);
            holder.picture.setImageBitmap(bitmap);
            //this is needed in order to cache the image and retrieve it for the expanded dialog
            holder.picture.setDrawingCacheEnabled(true);

        }else{
            //TODO some image like 'image not found' with a sad face :(

            Bitmap tempImage = BitmapFactory.decodeResource(context.getResources(),R.drawable.saddog);
            holder.picture.setImageBitmap(tempImage);
            //this is needed in order to cache the image and retrieve it for the expanded dialog
            holder.picture.setDrawingCacheEnabled(true);
        }

    }

    @Override
    public int getItemCount() {
        return itinerary.getItineraryMonuments() == null ? 0 : itinerary.getItineraryMonuments().length;
    }
}
