package org.apache.lucene.sandbox.postingshighlight;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.index.Term;
import org.apache.lucene.util.ArrayUtil;
import org.apache.lucene.util.RamUsageEstimator;

/**
 * Represents a passage (typically a sentence of the document). 
 * <p>
 * A passage contains {@link #getNumMatches} highlights from the query,
 * and the offsets and query terms that correspond with each match.
 * @lucene.experimental
 */
public final class Passage {
  int startOffset = -1;
  int endOffset = -1;
  float score = 0.0f;

  int matchStarts[] = new int[8];
  int matchEnds[] = new int[8];
  Term matchTerms[] = new Term[8];
  int numMatches = 0;
  
  void addMatch(int startOffset, int endOffset, Term term) {
    assert startOffset >= this.startOffset && startOffset <= this.endOffset;
    if (numMatches == matchStarts.length) {
      matchStarts = ArrayUtil.grow(matchStarts, numMatches+1);
      matchEnds = ArrayUtil.grow(matchEnds, numMatches+1);
      Term newMatchTerms[] = new Term[ArrayUtil.oversize(numMatches+1, RamUsageEstimator.NUM_BYTES_OBJECT_REF)];
      System.arraycopy(matchTerms, 0, newMatchTerms, 0, numMatches);
      matchTerms = newMatchTerms;
    }
    matchStarts[numMatches] = startOffset;
    matchEnds[numMatches] = endOffset;
    matchTerms[numMatches] = term;
    numMatches++;
  }
  
  void reset() {
    startOffset = endOffset = -1;
    score = 0.0f;
    numMatches = 0;
  }

  /**
   * Start offset of this passage.
   */
  public int getStartOffset() {
    return startOffset;
  }

  /**
   * End offset of this passage.
   */
  public int getEndOffset() {
    return endOffset;
  }

  /**
   * Passage's score.
   */
  public float getScore() {
    return score;
  }
  
  /**
   * Number of term matches available in 
   * {@link #getMatchStarts}, {@link #getMatchEnds}, 
   * {@link #getMatchTerms}
   */
  public int getNumMatches() {
    return numMatches;
  }

  /**
   * Start offsets of the term matches, in increasing order.
   * Only {@link #getNumMatches} are valid. Note that these
   * offsets are absolute (not relative to {@link #getStartOffset()}).
   */
  public int[] getMatchStarts() {
    return matchStarts;
  }

  /**
   * End offsets of the term matches, corresponding with
   * {@link #getMatchStarts}. Note that its possible that
   * an end offset could exceed beyond the bounds of the passage
   * ({@link #getEndOffset()}), if the Analyzer produced a term
   * which spans a passage boundary.
   */
  public int[] getMatchEnds() {
    return matchEnds;
  }

  /**
   * Term of the matches, corresponding with
   * {@link #getMatchStarts()}.
   */
  public Term[] getMatchTerms() {
    return matchTerms;
  }
}
