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
import es.iescarrillo.devsync.enums.ProjectStatus;
import es.iescarrillo.devsync.models.Project;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private final List<Project> projectList;
    private final OnProjectClickListener listener;

    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    public ProjectAdapter(List<Project> projectList, OnProjectClickListener listener) {
        this.projectList = projectList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProjectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_project, parent, false);
        return new ProjectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectViewHolder holder, int position) {
        Project project = projectList.get(position);

        holder.tvProjectName.setText(project.getName());
        holder.tvProjectDescription.setText(project.getDescription());

        if (project.getStatus() != null) {
            holder.tvProjectStatus.setText(project.getStatus().name());

            if (project.getStatus() == ProjectStatus.ACTIVE) {
                holder.tvProjectStatus.setBackgroundResource(R.drawable.bg_chip_blue);
                holder.tvProjectStatus.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.primary_700)
                );
            } else {
                holder.tvProjectStatus.setBackgroundResource(R.drawable.bg_chip_red);
                holder.tvProjectStatus.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.danger_600)
                );
            }
        } else {
            holder.tvProjectStatus.setText("");
        }

        holder.itemView.setOnClickListener(v -> listener.onProjectClick(project));
    }

    @Override
    public int getItemCount() {
        return projectList.size();
    }

    public static class ProjectViewHolder extends RecyclerView.ViewHolder {

        TextView tvProjectName, tvProjectDescription, tvProjectStatus;

        public ProjectViewHolder(@NonNull View itemView) {
            super(itemView);

            tvProjectName = itemView.findViewById(R.id.tvProjectName);
            tvProjectDescription = itemView.findViewById(R.id.tvProjectDescription);
            tvProjectStatus = itemView.findViewById(R.id.tvProjectStatus);
        }
    }
}
