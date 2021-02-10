package com.marvel.api.entity.response;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Response<T> {

  private T data;
  private List<String> messages;

  public Response(final T data) {
    this.data = data;
  }

  public Response(final List<String> messages) {
    this.messages = messages;
  }

  public Response(final String string) {
    final List<String> messagesList = new ArrayList<>();
    messagesList.add(string);
    this.messages = messagesList;
  }
}
