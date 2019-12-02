package it.dariocast.quadernotorneo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import it.dariocast.quadernotorneo.models.Partita;

class PartiteAdapter extends RecyclerView.Adapter<PartiteAdapter.PartitaViewHolder>{
    private List<Partita> partite;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class PartitaViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView squadraUno;
        TextView risultato;
        TextView squadraDue;
        LinearLayout view;
        PartitaViewHolder(LinearLayout ll) {
            super(ll);
            view = ll;
            squadraUno = ll.findViewById(R.id.squadraUno);
            squadraDue = ll.findViewById(R.id.squadraDue);
            risultato = ll.findViewById(R.id.risultato);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    PartiteAdapter(List<Partita> partite) {
        this.partite = partite;
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public PartiteAdapter.PartitaViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        LinearLayout item = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.partita_singola_layout, parent, false);
        return new PartitaViewHolder(item);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(PartitaViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Partita partita = partite.get(position);
        holder.squadraUno.setText(partita.getSquadraUno());
        holder.squadraDue.setText(partita.getSquadraDue());
        String risultato = partita.getGolSquadraUno() + ":" + partita.getGolSquadraDue();
        holder.risultato.setText(risultato);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DettaglioPartita.class);
                intent.putExtra("idPartita", partita.getId());
                context.startActivity(intent);
            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return partite.size();
    }
}
