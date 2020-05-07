<?php


class Dao {
  
  protected $mysqli;
  
  function Dao($mysqli) {
    PRECONDITION(get_class($mysqli)=="mon_mysqli");
    $this->mysqli = $mysqli;
  }
  
   public function getLevelsForSubject($subjectId, $language) {
    
    $sql ="select level.level_id, level.name FROM level, language, subject, subject_level " .
      " where level.language_id = language.language_id " .
      " and language.short_name='$language' " .
      " and subject_level.subject_id = subject.subject_id " .
      " and subject_level.level_id = level.level_id" . 
      " and subject.subject_id = $subjectId";
    
    $result = array();
    $i=0;
    
    $mysqli_result = $this->mysqli->query($sql);
    
    while($row=$mysqli_result->fetch_assoc()) {
      $result[$i] = $row;
      $i++;
    }

    return $result;
    
    
  }
  
  
  public function getSubjects($language) {
    $sql = "select subject.subject_id,subject.name from subject,language " .
      " where subject.language_id = language.language_id and language.short_name='" . $language . "'";
    
    $result = array();
    $i=0;
    
    $mysqli_result = $this->mysqli->query($sql);
    while($row=$mysqli_result->fetch_assoc()) {
      $result[$i]=$row;
      $i++;
    }

    return $result;
  }
  
  
  public function getSubjectsLevels($language) {
    $sql = "select subject.subject_id,subject.name from subject,language " .
      " where subject.language_id = language.language_id and language.short_name='" . $language . "'";
    
    $result = array();
    
    $mysqli_result = $this->mysqli->query($sql);
    while($row=$mysqli_result->fetch_assoc()) {
      $result[$row['subject_id']]=array('subject' => $row, 'levels' => $this->getLevelsForSubject($row['subject_id'], $language));

    }

    return $result;
  }
  
  
 
  
  
}


?>