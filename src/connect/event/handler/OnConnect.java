package connect.event.handler;

import connect.Connection;

public interface OnConnect {
  public void run(Connection connection);
}