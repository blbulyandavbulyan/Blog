package org.blbulyandavbulyan.blog.services;

import lombok.RequiredArgsConstructor;
import org.blbulyandavbulyan.blog.dtos.user.UserCreateRequest;
import org.blbulyandavbulyan.blog.dtos.user.UserInfoDTO;
import org.blbulyandavbulyan.blog.entities.User;
import org.blbulyandavbulyan.blog.exceptions.IllegalRoleNameException;
import org.blbulyandavbulyan.blog.exceptions.users.UserAlreadyExistsException;
import org.blbulyandavbulyan.blog.exceptions.users.UserNotFoundException;
import org.blbulyandavbulyan.blog.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Сервис для работы с пользователями
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    /**
     * Репозиторий с пользователями
     */
    private final UserRepository userRepository;
    /**
     * Репозиторий с ролями
     */
    private final RoleService roleService;
    /**
     * Шифровальщик паролей, используется для регистрации пользователей
     */
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод регистрирует пользователя в системе
     * @param userName имя пользователя
     * @param password пароль
     * @return зарегистрированного пользователя
     */
    public User registerUser(String userName, String password){
        if(!exists(userName)) {
            User user = new User(userName, passwordEncoder.encode(password));
            return userRepository.save(user);
        }
        else throw new UserAlreadyExistsException("User with name '" + userName + "' already exists!");
    }

    /**
     * Получает детали пользователя, необходимые для авторизации
     * @param username имя пользователя, детали которого нужно загрузить
     * @return найденные по имени детали
     * @throws UsernameNotFoundException если пользователь с данным именем не существует
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByName(username)
                .orElseThrow(()->new UsernameNotFoundException("user with given username not found!"));
    }

    /**
     * Проверяет, существует ли пользователь по имени
     * @param username имя пользователя, на существование которого нужно проверить
     * @return true если пользователь существует
     */
    public boolean exists(String username) {
        return userRepository.existsByName(username);
    }

    /**
     * Ищет пользователя по имени
     * @param userName имя пользователя
     * @return найденного по имени пользователя
     * @throws UserNotFoundException если пользователя с данным именем не существует
     */
    public User findByName(String userName) {
        return userRepository.findByName(userName).orElseThrow(()->new UserNotFoundException("user with " + userName + "was not found!"));
    }

    /**
     * Удаляет пользователя по ИД
     * @param id ИД пользователя, которого нужно удалить
     * @throws UserNotFoundException если пользователя с данным ИД не существует
     */
    public void deleteById(Long id) {
        if(userRepository.existsById(id)) userRepository.deleteById(id);
        else throw new UserNotFoundException("User with id " + id + " not found!");
    }

    /**
     * Метод получает ссылку на пользователя
     * @param name имя пользователя
     * @return полученную ссылку на пользователя
     */
    public User getReferenceByName(String name) {
        return userRepository.getReferenceByName(name);
    }

    /**
     * Метод создаёт пользователя по заданному запросу
     * @param userCreateRequest запрос, по которому нужно создать пользователя
     */
    @Transactional(rollbackFor = {UserAlreadyExistsException.class, IllegalRoleNameException.class})
    public void createUser(UserCreateRequest userCreateRequest) {
        User user = registerUser(userCreateRequest.name(), userCreateRequest.password());//регистрируем пользователя
        //а теперь устанавливаем ему необходимые привилегии
        for(String roleName : userCreateRequest.roleNames()){
            if(!roleService.existsByRoleName(roleName))throw new IllegalRoleNameException("Role with name + " + roleName + " not found!");
        }
        user.setRoles(userCreateRequest.roleNames().stream().map(roleService::getReferenceByRoleName).toList());
    }

    /**
     * Возвращает информацию о пользователе
     * @param userId ИД пользователя, информацию о котором нужно получить
     * @return искомую информацию о пользователе
     * @throws UserNotFoundException если пользователь не найден
     */
    public UserInfoDTO getUserInfo(Long userId) {
        return userRepository.findByUserId(userId, UserInfoDTO.class).orElseThrow(()->new UserNotFoundException("User with id " + " not found!"));
    }
}
