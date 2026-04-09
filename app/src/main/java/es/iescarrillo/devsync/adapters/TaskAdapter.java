package es.iescarrillo.devsync.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.enums.TaskStatus;
import es.iescarrillo.devsync.models.Task;
import es.iescarrillo.devsync.services.TaskService;

/**
 * Adaptador encargado de mostrar la lista de tareas.
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private final List<Task> taskList;
    private final String currentUserId;
    private TaskService taskService = new TaskService();

    public TaskAdapter(List<Task> taskList, String currentUserId) {
        this.taskList = taskList;
        this.currentUserId = currentUserId;
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

        holder.btnTaskAction.setVisibility(View.GONE);
        holder.btnTaskAction.setOnClickListener(null);

        if (task.getStatus() == TaskStatus.PENDING) {
            holder.tvTaskStatus.setText("PENDING");
            holder.tvTaskStatus.setBackgroundResource(R.drawable.bg_chip_yellow);
            holder.tvTaskStatus.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.task_pending)
            );
        } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            holder.tvTaskStatus.setText("IN_PROGRESS");
            holder.tvTaskStatus.setBackgroundResource(R.drawable.bg_chip_blue);
            holder.tvTaskStatus.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.task_in_progress)
            );
        } else if (task.getStatus() == TaskStatus.DONE) {
            holder.tvTaskStatus.setText("DONE");
            holder.tvTaskStatus.setBackgroundResource(R.drawable.bg_chip_green);
            holder.tvTaskStatus.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.task_done)
            );
        } else {
            holder.tvTaskStatus.setText("");
        }

        boolean isAssignedToCurrentUser = task.getAssignedDeveloper() != null
                && task.getAssignedDeveloper().equals(currentUserId);

        if (!isAssignedToCurrentUser) {
            return;
        }

        if (task.getStatus() == TaskStatus.PENDING) {
            holder.btnTaskAction.setVisibility(View.VISIBLE);
            holder.btnTaskAction.setText("Start");
            holder.btnTaskAction.setOnClickListener(v -> {
                task.setStatus(TaskStatus.IN_PROGRESS);
                taskService.updateTask(task);
            });
        } else if (task.getStatus() == TaskStatus.IN_PROGRESS) {
            holder.btnTaskAction.setVisibility(View.VISIBLE);
            holder.btnTaskAction.setText("Finish");
            holder.btnTaskAction.setOnClickListener(v -> {
                task.setStatus(TaskStatus.DONE);
                taskService.updateTask(task);
            });
        } else if (task.getStatus() == TaskStatus.DONE) {
            holder.btnTaskAction.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {

        TextView tvTaskTitle, tvTaskDescription, tvTaskStatus;
        MaterialButton btnTaskAction;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            tvTaskDescription = itemView.findViewById(R.id.tvTaskDescription);
            tvTaskStatus = itemView.findViewById(R.id.tvTaskStatus);
            btnTaskAction = itemView.findViewById(R.id.btnTaskAction);
        }
    }
}
