package com.nextpeyk.mobileapp.core.datastore;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineScope;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.nextpeyk.mobileapp.di.ApplicationScope")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class AppPreferences_Factory implements Factory<AppPreferences> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  private final Provider<CoroutineScope> scopeProvider;

  public AppPreferences_Factory(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<CoroutineScope> scopeProvider) {
    this.dataStoreProvider = dataStoreProvider;
    this.scopeProvider = scopeProvider;
  }

  @Override
  public AppPreferences get() {
    return newInstance(dataStoreProvider.get(), scopeProvider.get());
  }

  public static AppPreferences_Factory create(Provider<DataStore<Preferences>> dataStoreProvider,
      Provider<CoroutineScope> scopeProvider) {
    return new AppPreferences_Factory(dataStoreProvider, scopeProvider);
  }

  public static AppPreferences newInstance(DataStore<Preferences> dataStore, CoroutineScope scope) {
    return new AppPreferences(dataStore, scope);
  }
}
