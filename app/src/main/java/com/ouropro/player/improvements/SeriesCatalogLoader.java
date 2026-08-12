package com.ouropro.player.improvements;

import androidx.annotation.NonNull;

import com.ouropro.player.models.CategoryModel;
import com.ouropro.player.models.SeriesModel;
import com.ouropro.player.remote.APIService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Carrega o catálogo real de séries por categoria, sem aceitar uma resposta parcial global. */
public final class SeriesCatalogLoader {
    private static final int MAX_IN_FLIGHT = 4;

    public interface Listener {
        void onComplete(List<SeriesModel> models, List<CategoryModel> categories);
        void onFailure(String message);
    }

    private SeriesCatalogLoader() {
    }

    public static void load(APIService api, String username, String password, Listener listener) {
        api.get_series_categories(username, password).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                List<CategoryModel> categories = response.body();
                if (!response.isSuccessful() || categories == null || categories.isEmpty()) {
                    loadGlobalFallback(api, username, password, listener, "O servidor não retornou categorias de séries");
                    return;
                }
                List<CategoryModel> validCategories = new ArrayList<>();
                for (CategoryModel category : categories) {
                    if (category != null && category.getId() != null && !category.getId().trim().isEmpty()) {
                        validCategories.add(category);
                    }
                }
                if (validCategories.isEmpty()) {
                    loadGlobalFallback(api, username, password, listener, "As categorias de séries não possuem IDs válidos");
                    return;
                }
                loadCategories(api, username, password, validCategories, listener);
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable throwable) {
                loadGlobalFallback(api, username, password, listener, "Falha ao consultar categorias de séries");
            }
        });
    }

    private static void loadCategories(APIService api, String username, String password, List<CategoryModel> categories, Listener listener) {
        LoaderState state = new LoaderState(api, username, password, categories, listener);
        state.start();
    }

    private static void loadGlobalFallback(APIService api, String username, String password, Listener listener, String reason) {
        api.get_second_series(username, password).enqueue(new Callback<List<SeriesModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<SeriesModel>> call, @NonNull Response<List<SeriesModel>> response) {
                List<SeriesModel> body = response.body();
                if (response.isSuccessful() && body != null && !body.isEmpty()) {
                    listener.onComplete(body, new ArrayList<>());
                } else {
                    listener.onFailure(reason);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SeriesModel>> call, @NonNull Throwable throwable) {
                listener.onFailure(reason);
            }
        });
    }

    private static final class LoaderState {
        private final APIService api;
        private final String username;
        private final String password;
        private final List<CategoryModel> categories;
        private final Listener listener;
        private final Map<String, SeriesModel> models = new LinkedHashMap<>();
        private int nextIndex;
        private int active;
        private int finished;

        private LoaderState(APIService api, String username, String password, List<CategoryModel> categories, Listener listener) {
            this.api = api;
            this.username = username;
            this.password = password;
            this.categories = categories;
            this.listener = listener;
        }

        private synchronized void start() {
            while (active < MAX_IN_FLIGHT && nextIndex < categories.size()) {
                launchNext();
            }
        }

        private synchronized void launchNext() {
            CategoryModel category = categories.get(nextIndex++);
            active++;
            api.get_series_by_category(username, password, category.getId()).enqueue(new Callback<List<SeriesModel>>() {
                @Override
                public void onResponse(@NonNull Call<List<SeriesModel>> call, @NonNull Response<List<SeriesModel>> response) {
                    synchronized (LoaderState.this) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (SeriesModel model : response.body()) {
                                if (model == null) {
                                    continue;
                                }
                                if (model.getCategory_id() == null || model.getCategory_id().isEmpty()) {
                                    model.setCategory_id(category.getId());
                                }
                                if (model.getCategory_name() == null || model.getCategory_name().isEmpty()) {
                                    model.setCategory_name(category.getName());
                                }
                                String id = model.getSeries_id();
                                if (id == null || id.isEmpty()) {
                                    id = model.getName() + "|" + category.getId();
                                }
                                models.put(id, model);
                            }
                        }
                        finishOne();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<SeriesModel>> call, @NonNull Throwable throwable) {
                    synchronized (LoaderState.this) {
                        finishOne();
                    }
                }
            });
        }

        private void finishOne() {
            active--;
            finished++;
            while (active < MAX_IN_FLIGHT && nextIndex < categories.size()) {
                launchNext();
            }
            if (finished >= categories.size() && active == 0) {
                if (models.isEmpty()) {
                    listener.onFailure("Nenhuma série foi retornada pelas categorias");
                } else {
                    listener.onComplete(new ArrayList<>(models.values()), categories);
                }
            }
        }
    }
}
