package io.github.catuhana.enchk.interfaces;

import com.mojang.brigadier.suggestion.Suggestion;
import io.github.catuhana.enchk.command.Flag;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface IClientSuggestionsProvider {
    <T> T enchk_getFlag(Flag<T> arg);

    <T> IClientSuggestionsProvider enchk_withFlag(Flag<T> arg, T value);

    @Nullable
    List<Suggestion> enchk_filterSuggestions(List<Suggestion> suggestions);
}
