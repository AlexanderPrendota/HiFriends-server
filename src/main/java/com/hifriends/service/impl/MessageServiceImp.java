package com.hifriends.service.impl;

import com.hifriends.exception.PostMessageException;
import com.hifriends.model.Chat;
import com.hifriends.model.Message;
import com.hifriends.model.User;
import com.hifriends.model.dto.MessageDto;
import com.hifriends.model.dto.MessagePostDto;
import com.hifriends.repository.ChatRepository;
import com.hifriends.repository.MessageRepository;
import com.hifriends.repository.UserRepository;
import com.hifriends.service.api.MessageService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author by aleksandrprendota on 24.08.17.
 */
@Service
public class MessageServiceImp implements MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Post message entity to db
     *
     * @param messagePostDto
     */
    @Override
    public MessageDto postMessage(MessagePostDto messagePostDto) {
        User sender = userRepository.findOne(messagePostDto.getSenderId());
        Chat chat = chatRepository.findOne(messagePostDto.getChatId());
        int countOfCharacters = messagePostDto.getText().length();
        if (sender != null && chat != null && countOfCharacters <= 255) {
            Message message = Message.builder()
                    .chat(chat)
                    .sender(sender)
                    .text(messagePostDto.getText())
                    .timeStamp(messagePostDto.getTimeStamp()).build();
            messageRepository.save(message);
            return convertToDto(message);
        } else {
            throw new PostMessageException("Problems with sending message");
        }
    }

    /**
     * Getting list of message by chat entity
     *
     * @param id chat entity
     * @return
     */
    @Override
    public List<MessageDto> findByChat(Chat id) {
        return messageRepository.findByChatOrderByTimeStamp(id)
                .stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private MessageDto convertToDto(Message message) {
        return modelMapper.map(message, MessageDto.class);
    }
}