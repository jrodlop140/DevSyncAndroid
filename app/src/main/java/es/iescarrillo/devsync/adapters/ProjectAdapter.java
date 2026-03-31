package es.iescarrillo.devsync.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.iescarrillo.devsync.R;
import es.iescarrillo.devsync.models.Project;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ProjectViewHolder> {

    private List<Project> projectList;

    public ProjectAdapter(List<Project> projectList) {
        this.projectList = projectList;
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
        } else {
            holder.tvProjectStatus.setText("");
        }
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