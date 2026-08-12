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

/** Carrega o catálogo de séries preservando o caminho global do APK original e usando categorias como fallback. */
public final class SeriesCatalogLoader {
    private static final int MAX_IN_FLIGHT = 8;
    private static final int MIN_COMPLETE_CATALOG = 100;

    public interface Listener {
        void onComplete(List<SeriesModel> models, List<CategoryModel> categories);
        void onFailure(String message);
    }

    private SeriesCatalogLoader() {
    }

    public static void load(APIService api, String username, String password, Listener listener) {
        // O APK original usava esta rota e, para a maioria dos servidores, ela é a mais rápida.
        api.get_series(username, password).enqueue(new Callback<List<SeriesModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<SeriesModel>> call, @NonNull Response<List<SeriesModel>> response) {
                List<SeriesModel> body = response.body();
                if (response.isSuccessful() && body != null && body.size() >= MIN_COMPLETE_CATALOG) {
                    listener.onComplete(body, new ArrayList<>());
                    return;
                }
                loadByCategories(api, username, password, listener, body);
            }

            @Override
            public void onFailure(@NonNull Call<List<SeriesModel>> call, @NonNull Throwable throwable) {
                loadByCategories(api, username, password, listener, null);
            }
        });
    }

    private static void loadByCategories(APIService api, String username, String password, Listener listener, List<SeriesModel> partialGlobal) {
        api.get_series_categories(username, password).enqueue(new Callback<List<CategoryModel>>() {
            @Override
            public void onResponse(@NonNull Call<List<CategoryModel>> call, @NonNull Response<List<CategoryModel>> response) {
                List<CategoryModel> categories = response.body();
                if (!response.isSuccessful() || categories == null || categories.isEmpty()) {
                    failOrKeepPartial(listener, partialGlobal, "O servidor retornou apenas um subconjunto de séries e não forneceu categorias");
                    return;
                }
                List<CategoryModel> validCategories = new ArrayList<>();
                for (CategoryModel category : categories) {
                    if (category != null && category.getId() != null && !category.getId().trim().isEmpty()) {
                        validCategories.add(category);
                    }
                }
                if (validCategories.isEmpty()) {
                    failOrKeepPartial(listener, partialGlobal, "As categorias de séries não possuem IDs válidos");
                    return;
                }
                new LoaderState(api, username, password, validCategories, partialGlobal, listener).start();
            }

            @Override
            public void onFailure(@NonNull Call<List<CategoryModel>> call, @NonNull Throwable throwable) {
                failOrKeepPartial(listener, partialGlobal, "Falha ao consultar categorias de séries");
            }
        });
    }

    private static void failOrKeepPartial(Listener listener, List<SeriesModel> partialGlobal, String message) {
        // Não entrega uma lista parcial como se fosse completa. O chamador preserva o cache existente.
        listener.onFailure(message);
    }

    private static final class LoaderState {
        private final APIService api;
        private final String username;
        private final String password;
        private final List<CategoryModel> categories;
        private final List<SeriesModel> partialGlobal;
        private final Listener listener;
        private final Map<String, SeriesModel> models = new LinkedHashMap<>();
        private int nextIndex;
        private int active;
        private int finished;

        private LoaderState(APIService api, String username, String password, List<CategoryModel> categories, List<SeriesModel> partialGlobal, Listener listener) {
            this.api = api;
            this.username = username;
            this.password = password;
            this.categories = categories;
            this.partialGlobal = partialGlobal;
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
                if (models.size() >= MIN_COMPLETE_CATALOG) {
                    listener.onComplete(new ArrayList<>(models.values()), categories);
                } else {
                    listener.onFailure("O servidor retornou apenas " + models.size() + " séries nas categorias");
                }
            }
        }
    }
}
