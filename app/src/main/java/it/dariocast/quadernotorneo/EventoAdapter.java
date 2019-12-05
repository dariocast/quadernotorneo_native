package it.dariocast.quadernotorneo;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import it.dariocast.quadernotorneo.models.Evento;

public class EventoAdapter extends RecyclerView.Adapter<EventoAdapter.MyViewHolder> {
    private List<Evento> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView textView;
        public MyViewHolder(TextView v) {
            super(v);
            textView = v;
            Typeface face= Typeface.createFromAsset(v.getContext().getAssets(), "font.ttf");
            v.setTypeface(face);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public EventoAdapter(List<Evento> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public EventoAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        TextView v = (TextView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.evento_text_view, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Evento evento = mDataset.get(position);
        String eventoStr = evento.getNome()+" - "+evento.getTipo();
        holder.textView.setText(eventoStr);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}