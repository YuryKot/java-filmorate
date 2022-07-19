package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
public class InMemoryMpaStorage implements MpaStorage {

    public final static Map<Integer, Mpa> MPA = new TreeMap<>() {{
        put(1, new Mpa(1, "G"));
        put(2, new Mpa(2, "PG"));
        put(3, new Mpa(3, "PG-13"));
        put(4, new Mpa(4, "R"));
        put(5, new Mpa(5, "NC-17"));
    }};

    @Override
    public List<Mpa> getMpa() {
        return new ArrayList<>(MPA.values());
    }

    @Override
    public Mpa getMpaById(int id) {
        if (!MPA.containsKey(id)) {
            throw new NotFoundException(String.format("Рейтинг с id = %d не найден", id));
        }
        return MPA.get(id);
    }
}
