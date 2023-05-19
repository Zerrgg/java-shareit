package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailConflictException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.User;

import java.util.*;

@Repository
public class UserDaoImpl implements UserDao {
    public static final String USER_NOT_FOUND_MESSAGE = "не найден пользователь с id: ";
    public static final String EMAIL_CONFLICT_MESSAGE = "email уже используется другим пользователем: ";

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private long currentId = 1;

    @Override
    public User add(User user) {
        checkEmail(user.getEmail());
        user.setId(generateId());
        users.put(user.getId(), user);
        emails.add(user.getEmail());
        return user;
    }

    @Override
    public User update(Long id, User user) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id);
        }
        User oldEntry = users.get(id);

        if (user.getName() != null) {
            oldEntry.setName(user.getName());
        }

        String oldEmail = users.get(id).getEmail();
        String newEmail = user.getEmail();
        if (newEmail != null && !oldEmail.equals(newEmail)) {
            updateEmail(oldEmail, newEmail);
            oldEntry.setEmail(newEmail);
        }
        return oldEntry;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        if (!users.containsKey(id)) throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE + id);
        emails.remove(users.get(id).getEmail());
        users.remove(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    private void checkEmail(String email) {
        if (emails.contains(email)) throw new EmailConflictException(EMAIL_CONFLICT_MESSAGE + email);
    }

    private void updateEmail(String oldEmail, String newEmail) {
        emails.remove(oldEmail);
        if (emails.contains(newEmail)) {
            emails.add(oldEmail);
            throw new EmailConflictException(EMAIL_CONFLICT_MESSAGE + newEmail);
        }
        emails.add(newEmail);
    }

    private Long generateId() {
        return currentId++;
    }
}
