package net.consensys.tools.ipfs.ipfsstore.dao;

import net.consensys.tools.ipfs.ipfsstore.exception.DaoException;

/**
 * PinningStrategy represents a strategy for keeping the content stored in the node
 *
 * @author Gregoire Jeanmart <gregoire.jeanmart@consensys.net>
 */
public interface PinningStrategy {

  /**
   * Return the name of the strategy
   * @return Name of the service
   */
  String getName();
  
  /**
   * Pin the content
   *
   * @param hash  Unique identifier of the file
   * @throws DaoException
   */
  void pin(String hash) throws DaoException;

  /**
   * Unpin the content
   *
   * @param hash  Unique identifier of the file
   * @throws DaoException
   */
  void unpin(String hash) throws DaoException;
  
}
