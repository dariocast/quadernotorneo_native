package it.dariocast.quadernotorneo;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.dariocast.quadernotorneo.models.Giocatore;

class GiocatoriAdapter extends RecyclerView.Adapter<GiocatoriAdapter.GiocatoreViewHolder>{
    private List<Giocatore> giocatori;


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class GiocatoreViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView nome;
        GiocatoreViewHolder(TextView tv) {
            super(tv);
            nome = tv;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    GiocatoriAdapter(List<Giocatore> giocatori) {
        this.giocatori = giocatori;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public GiocatoreViewHolder onCreateViewHolder(ViewGroup parent,
                                                  int viewType) {
        // create a new view
        TextView item = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.giocatore_layout, parent, false);
        return new GiocatoreViewHolder(item);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(GiocatoreViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Giocatore giocatore = giocatori.get(position);
        holder.nome.setText(giocatore.getNome());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return giocatori.size();
    }
}
