package es.iescarrillo.devsync.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.enums.RequestState;
import es.iescarrillo.devsync.models.RequestItem;

/**
 * Adaptador encargado de mostrar la lista de solicitudes del usuario.
 */
public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {

    private final List<RequestItem> requestList;

    public RequestAdapter(List<RequestItem> requestList) {
        this.requestList = requestList;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        RequestItem request = requestList.get(position);

        holder.tvProjectName.setText(request.getProjectName());
        holder.tvRequestDate.setText(formatDate(request.getRequestDate()));

        if (request.getState() != null) {
            holder.tvState.setText(request.getState().name());

            if (request.getState() == RequestState.PENDING) {
                holder.tvState.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_orange_dark)
                );
            } else if (request.getState() == RequestState.ACCEPTED) {
                holder.tvState.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_green_dark)
                );
            } else if (request.getState() == RequestState.REJECTED) {
                holder.tvState.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_red_dark)
                );
            }
        } else {
            holder.tvState.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView tvProjectName, tvState, tvRequestDate;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProjectName = itemView.findViewById(R.id.tvRequestProjectName);
            tvState = itemView.findViewById(R.id.tvRequestState);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
        }
    }

    /**
     * Método auxiliar para formatear la fecha de la solicitud.
     *
     * @param timestampString Fecha en formato timestamp.
     * @return Retorna la fecha formateada.
     */
    private String formatDate(String timestampString) {
        try {
            long timestamp = Long.parseLong(timestampString);

            java.text.SimpleDateFormat sdf =
                    new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault());

            return sdf.format(new java.util.Date(timestamp));

        } catch (Exception e) {
            return "";
        }
    }
}