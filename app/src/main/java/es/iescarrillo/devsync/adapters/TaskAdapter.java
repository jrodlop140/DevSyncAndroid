package es.iescarrillo.devsync.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.models.Task;

/**
 * Adaptador encargado de mostrar la lista de tareas.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;

    /**
     * Constructor del adaptador.
     *
     * @param taskList Lista de tareas.
     */
    public TaskAdapter(List<Task> taskList) {
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task task = taskList.get(position);

        holder.tvTaskTitle.setText(task.getTitle());
        holder.tvTaskDescription.setText(task.getDescription());

        if (task.getStatus() != null) {
            holder.tvTaskStatus.setText(task.getStatus().name());
        } else {
            holder.tvTaskStatus.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    /**
     * ViewHolder de cada tarea.
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskTitle, tvTaskDescription, tvTaskStatus;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskStatus = itemView.findViewById(R.id.tvTaskStatus);
        }
    }
}