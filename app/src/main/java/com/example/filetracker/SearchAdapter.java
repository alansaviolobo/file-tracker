//package com.example.filetracker;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//import java.util.List;
//
//public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {
//
//    private List<SearchResult> searchResults;
//
//    public void setSearchResults(List<SearchResult> searchResults) {
//        this.searchResults = searchResults;
//        notifyDataSetChanged(); // Notify RecyclerView that the dataset has changed
//    }
//
//    @NonNull
//    @Override
//    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
//        return new SearchViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
//        SearchResult result = searchResults.get(position);
//        holder.bind(result);
//    }
//
//    @Override
//    public int getItemCount() {
//        return searchResults == null ? 0 : searchResults.size();
//    }
//
//    static class SearchViewHolder extends RecyclerView.ViewHolder {
//
//        private TextView codeTextView;
//        private TextView dateTextView;
//        private TextView personTextView;
//
//        SearchViewHolder(@NonNull View itemView) {
//            super(itemView);
//            codeTextView = itemView.findViewById(R.id.code_text_view);
//            dateTextView = itemView.findViewById(R.id.date_text_view);
//            personTextView = itemView.findViewById(R.id.person_text_view);
//        }
//
//        void bind(SearchResult result) {
//            codeTextView.setText(result.getCode());
//            dateTextView.setText(result.getDate());
//            personTextView.setText(result.getPerson());
//        }
//    }
//}
