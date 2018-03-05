/*
 * Params.h
 *
 *  Created on: Mar 5, 2018
 *      Author: kelvin
 */

#ifndef PARAMS_H_
#define PARAMS_H_

#define WORD_BYTES 4                    //bytes in word
#define DATASET_BYTES_INIT  1L << 30        //bytes in dataset at genesis
#define DATASET_BYTES_GROWTH 1L << 23      //dataset growth per epoch
#define CACHE_BYTES_INIT 1L << 24         //bytes in cache at genesis
#define CACHE_BYTES_GROWTH 1L << 17        //cache growth per epoch
#define CACHE_MULTIPLIER 1024             //Size of the DAG relative to the cache
#define EPOCH_LENGTH 30000              //blocks per epoch
#define MIX_BYTES 128                   //width of mix
#define HASH_BYTES 64                   //hash length in bytes
#define DATASET_PARENTS 256             //number of parents of each dataset element
#define CACHE_ROUNDS 3                  //number of rounds in cache production
#define ACCESSES 64                     //number of accesses in hashimoto loop



#endif /* PARAMS_H_ */
