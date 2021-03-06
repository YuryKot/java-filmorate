package ru.yandex.practicum.filmorate.service.Impl;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Service
public class MpaServiceImpl implements MpaService {

    private final MpaStorage mpaStorage;

    public MpaServiceImpl(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public List<Mpa> getMpa() {
        return mpaStorage.getMpa();
    }

    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id);
    }
}
