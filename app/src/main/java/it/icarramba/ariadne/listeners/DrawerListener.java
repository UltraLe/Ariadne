package it.icarramba.ariadne.listeners;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;

import it.icarramba.ariadne.LastItinerariesActivity;
import it.icarramba.ariadne.R;
import it.icarramba.ariadne.RicercaActivity;
import it.icarramba.ariadne.SavedItinerariesActivity;
import it.icarramba.ariadne.SearchedItinerariesActivity;

public class DrawerListener implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;

    public DrawerListener(Context context) {
        this.context = context;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.ricerca:
                context.startActivity(new Intent(context, RicercaActivity.class));
                break;
            case R.id.saved:
                context.startActivity(new Intent(context, SavedItinerariesActivity.class));
                break;
            case R.id.lasts:
                context.startActivity(new Intent(context, LastItinerariesActivity.class));
                break;
            default:
                return true;
        }


        return true;
    }

}

