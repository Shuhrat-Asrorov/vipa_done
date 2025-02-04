package com.example.vipa.service;

import com.example.vipa.dto.ClientDetailsDto;
import com.example.vipa.exception.NotFoundException;
import com.example.vipa.mapping.ClientMapper;
import com.example.vipa.model.Client;
import com.example.vipa.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j // для логирования
@Service // = Component (чтобы автоматически создать объект класса ClientService и поместить его в контекст приложения Spring)
@RequiredArgsConstructor // для автоматической генерации конструктора со всеми финальными полями
public class ClientService {

    private static final String CLIENT_NOT_FOUND_MESSAGE = "Пользователь с указанным id не найден.";
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ClientMapper clientMapper; // для преобразования из ClientDetailsDto в Client и наоборот
    private final ClientRepository clientRepository;
    /**
     * Метод для получения пользователя по его id.
     * @param clientId - id пользователя
     * @return - возвращаем объект класса ClientDetailsDto
     */
    public ClientDetailsDto getClient(int clientId) {
        log.info("inside getClient(), clientId: {}", clientId);
        return clientRepository.findById(clientId).map(clientMapper::convertToClientDetailsDto)
                .orElseThrow(() -> new NotFoundException(CLIENT_NOT_FOUND_MESSAGE));
    }

    /**
     * Метод для получения пользователя по его id.
     * @param clientId - id пользователя
     * @return - возвращаем объект класса Client
     */
    public Client getClientEntity(int clientId) {
        log.info("inside getClient(), clientId: {}", clientId);
        return clientRepository.findById(clientId)
                .orElseThrow(() -> new NotFoundException(CLIENT_NOT_FOUND_MESSAGE));
    }

    /**
     * Метод для сохранения пользователя.
     * @param updatedClient - пользователь, которого нужно обновить
     */
    public void updateClient(Client updatedClient) {
        clientRepository.save(updatedClient);
    }

    /**
     * Метод для обновления данных пользователя.
     * @param clientId - id пользователя, данные которого нужно обновить
     * @param clientDetailsDto - обновленные данные пользователя
     * @return - возвращаем объекта класса ClientDetailsDto (данные обновленного пользователя)
     */
    public ClientDetailsDto updateClient(int clientId, ClientDetailsDto clientDetailsDto) {
        log.info("inside updateClient(), clientId: {}, clientDetailsDto: {}", clientId, clientDetailsDto);
        Client client = clientRepository.findById(clientId)
                .orElseThrow(()->new NotFoundException(CLIENT_NOT_FOUND_MESSAGE));
        Client updatedClient = clientMapper.convertToClient(clientDetailsDto);
        if(clientDetailsDto.getPassword().isEmpty()){
            updatedClient.setPassword(client.getPassword());
        } else {
            updatedClient.setPassword(bCryptPasswordEncoder.encode(updatedClient.getPassword()));
        }
        /* Чтобы понять, какого именно клиента нужно обновить, нужно присвоить clientId.
           Если не сделать это, то вместо обновления существующего клиента будет создан новый клиент.*/
        updatedClient.setId(clientId);
        return clientMapper.convertToClientDetailsDto(clientRepository.save(updatedClient));
    }

    /**
     * Метод для удаления пользователя по id.
     * @param clientId - id пользователя, которого нужно удалить.
     */
    public void deleteClient(int clientId) {
        log.info("inside deleteClient(), clientId: {}", clientId);
        clientRepository.deleteById(clientId);
    }
}
