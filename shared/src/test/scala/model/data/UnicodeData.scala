package model.data

object UnicodeData {

  val graphemeTests: String =
    s"""
       |÷ 0020 ÷ 0020 ÷
       |÷ 0020 × 0308 ÷ 0020 ÷	
       |÷ 0020 ÷ 000D ÷	
       |÷ 0020 × 0308 ÷ 000D ÷	
       |÷ 0020 ÷ 000A ÷	
       |÷ 0020 × 0308 ÷ 000A ÷	
       |÷ 0020 ÷ 0001 ÷	
       |÷ 0020 × 0308 ÷ 0001 ÷	
       |÷ 0020 × 0300 ÷	
       |÷ 0020 × 0308 × 0300 ÷	
       |÷ 0020 ÷ 0600 ÷	
       |÷ 0020 × 0308 ÷ 0600 ÷	
       |÷ 0020 × 0903 ÷	
       |÷ 0020 × 0308 × 0903 ÷	
       |÷ 0020 ÷ 1100 ÷	
       |÷ 0020 × 0308 ÷ 1100 ÷	
       |÷ 0020 ÷ 1160 ÷	
       |÷ 0020 × 0308 ÷ 1160 ÷	
       |÷ 0020 ÷ 11A8 ÷	
       |÷ 0020 × 0308 ÷ 11A8 ÷	
       |÷ 0020 ÷ AC00 ÷	
       |÷ 0020 × 0308 ÷ AC00 ÷	
       |÷ 0020 ÷ AC01 ÷	
       |÷ 0020 × 0308 ÷ AC01 ÷	
       |÷ 0020 ÷ 1F1E6 ÷	
       |÷ 0020 × 0308 ÷ 1F1E6 ÷	
       |÷ 0020 ÷ 261D ÷	
       |÷ 0020 × 0308 ÷ 261D ÷	
       |÷ 0020 ÷ 1F3FB ÷	
       |÷ 0020 × 0308 ÷ 1F3FB ÷	
       |÷ 0020 × 200D ÷	
       |÷ 0020 × 0308 × 200D ÷	
       |÷ 0020 ÷ 2640 ÷	
       |÷ 0020 × 0308 ÷ 2640 ÷	
       |÷ 0020 ÷ 1F466 ÷	
       |÷ 0020 × 0308 ÷ 1F466 ÷	
       |÷ 0020 ÷ 0378 ÷	
       |÷ 0020 × 0308 ÷ 0378 ÷	
       |÷ 0020 ÷ D800 ÷	
       |÷ 0020 × 0308 ÷ D800 ÷	
       |÷ 000D ÷ 0020 ÷	
       |÷ 000D ÷ 0308 ÷ 0020 ÷
       |÷ 000D ÷ 000D ÷
       |÷ 000D ÷ 0308 ÷ 000D ÷	
       |÷ 000D × 000A ÷	
       |÷ 000D ÷ 0308 ÷ 000A ÷	
       |÷ 000D ÷ 0001 ÷	
       |÷ 000D ÷ 0308 ÷ 0001 ÷	
       |÷ 000D ÷ 0300 ÷	
       |÷ 000D ÷ 0308 × 0300 ÷	
       |÷ 000D ÷ 0600 ÷	
       |÷ 000D ÷ 0308 ÷ 0600 ÷	
       |÷ 000D ÷ 0903 ÷	
       |÷ 000D ÷ 0308 × 0903 ÷	
       |÷ 000D ÷ 1100 ÷	
       |÷ 000D ÷ 0308 ÷ 1100 ÷	
       |÷ 000D ÷ 1160 ÷	
       |÷ 000D ÷ 0308 ÷ 1160 ÷	
       |÷ 000D ÷ 11A8 ÷	
       |÷ 000D ÷ 0308 ÷ 11A8 ÷	
       |÷ 000D ÷ AC00 ÷	
       |÷ 000D ÷ 0308 ÷ AC00 ÷	
       |÷ 000D ÷ AC01 ÷	
       |÷ 000D ÷ 0308 ÷ AC01 ÷	
       |÷ 000D ÷ 1F1E6 ÷	
       |÷ 000D ÷ 0308 ÷ 1F1E6 ÷	
       |÷ 000D ÷ 261D ÷	
       |÷ 000D ÷ 0308 ÷ 261D ÷	
       |÷ 000D ÷ 1F3FB ÷	
       |÷ 000D ÷ 0308 ÷ 1F3FB ÷	
       |÷ 000D ÷ 200D ÷	
       |÷ 000D ÷ 0308 × 200D ÷	
       |÷ 000D ÷ 2640 ÷	
       |÷ 000D ÷ 0308 ÷ 2640 ÷	
       |÷ 000D ÷ 1F466 ÷	
       |÷ 000D ÷ 0308 ÷ 1F466 ÷	
       |÷ 000D ÷ 0378 ÷	
       |÷ 000D ÷ 0308 ÷ 0378 ÷	
       |÷ 000D ÷ D800 ÷	
       |÷ 000D ÷ 0308 ÷ D800 ÷	
       |÷ 000A ÷ 0020 ÷	
       |÷ 000A ÷ 0308 ÷ 0020 ÷	
       |÷ 000A ÷ 000D ÷	
       |÷ 000A ÷ 0308 ÷ 000D ÷	
       |÷ 000A ÷ 000A ÷	
       |÷ 000A ÷ 0308 ÷ 000A ÷	
       |÷ 000A ÷ 0001 ÷	
       |÷ 000A ÷ 0308 ÷ 0001 ÷	
       |÷ 000A ÷ 0300 ÷	
       |÷ 000A ÷ 0308 × 0300 ÷	
       |÷ 000A ÷ 0600 ÷	
       |÷ 000A ÷ 0308 ÷ 0600 ÷	
       |÷ 000A ÷ 0903 ÷	
       |÷ 000A ÷ 0308 × 0903 ÷	
       |÷ 000A ÷ 1100 ÷	
       |÷ 000A ÷ 0308 ÷ 1100 ÷	
       |÷ 000A ÷ 1160 ÷	
       |÷ 000A ÷ 0308 ÷ 1160 ÷	
       |÷ 000A ÷ 11A8 ÷	
       |÷ 000A ÷ 0308 ÷ 11A8 ÷	
       |÷ 000A ÷ AC00 ÷	
       |÷ 000A ÷ 0308 ÷ AC00 ÷	
       |÷ 000A ÷ AC01 ÷	
       |÷ 000A ÷ 0308 ÷ AC01 ÷	
       |÷ 000A ÷ 1F1E6 ÷	
       |÷ 000A ÷ 0308 ÷ 1F1E6 ÷	
       |÷ 000A ÷ 261D ÷	
       |÷ 000A ÷ 0308 ÷ 261D ÷	
       |÷ 000A ÷ 1F3FB ÷	
       |÷ 000A ÷ 0308 ÷ 1F3FB ÷	
       |÷ 000A ÷ 200D ÷	
       |÷ 000A ÷ 0308 × 200D ÷	
       |÷ 000A ÷ 2640 ÷	
       |÷ 000A ÷ 0308 ÷ 2640 ÷	
       |÷ 000A ÷ 1F466 ÷	
       |÷ 000A ÷ 0308 ÷ 1F466 ÷	
       |÷ 000A ÷ 0378 ÷	
       |÷ 000A ÷ 0308 ÷ 0378 ÷	
       |÷ 000A ÷ D800 ÷	
       |÷ 000A ÷ 0308 ÷ D800 ÷	
       |÷ 0001 ÷ 0020 ÷	
       |÷ 0001 ÷ 0308 ÷ 0020 ÷	
       |÷ 0001 ÷ 000D ÷	
       |÷ 0001 ÷ 0308 ÷ 000D ÷	
       |÷ 0001 ÷ 000A ÷	
       |÷ 0001 ÷ 0308 ÷ 000A ÷	
       |÷ 0001 ÷ 0001 ÷	
       |÷ 0001 ÷ 0308 ÷ 0001 ÷	
       |÷ 0001 ÷ 0300 ÷	
       |÷ 0001 ÷ 0308 × 0300 ÷	
       |÷ 0001 ÷ 0600 ÷	
       |÷ 0001 ÷ 0308 ÷ 0600 ÷	
       |÷ 0001 ÷ 0903 ÷	
       |÷ 0001 ÷ 0308 × 0903 ÷	
       |÷ 0001 ÷ 1100 ÷	
       |÷ 0001 ÷ 0308 ÷ 1100 ÷	
       |÷ 0001 ÷ 1160 ÷	
       |÷ 0001 ÷ 0308 ÷ 1160 ÷	
       |÷ 0001 ÷ 11A8 ÷	
       |÷ 0001 ÷ 0308 ÷ 11A8 ÷	
       |÷ 0001 ÷ AC00 ÷	
       |÷ 0001 ÷ 0308 ÷ AC00 ÷	
       |÷ 0001 ÷ AC01 ÷	
       |÷ 0001 ÷ 0308 ÷ AC01 ÷	
       |÷ 0001 ÷ 1F1E6 ÷	
       |÷ 0001 ÷ 0308 ÷ 1F1E6 ÷	
       |÷ 0001 ÷ 261D ÷	
       |÷ 0001 ÷ 0308 ÷ 261D ÷	
       |÷ 0001 ÷ 1F3FB ÷	
       |÷ 0001 ÷ 0308 ÷ 1F3FB ÷	
       |÷ 0001 ÷ 200D ÷	
       |÷ 0001 ÷ 0308 × 200D ÷	
       |÷ 0001 ÷ 2640 ÷	
       |÷ 0001 ÷ 0308 ÷ 2640 ÷	
       |÷ 0001 ÷ 1F466 ÷	
       |÷ 0001 ÷ 0308 ÷ 1F466 ÷	
       |÷ 0001 ÷ 0378 ÷	
       |÷ 0001 ÷ 0308 ÷ 0378 ÷	
       |÷ 0001 ÷ D800 ÷	
       |÷ 0001 ÷ 0308 ÷ D800 ÷	
       |÷ 0300 ÷ 0020 ÷	
       |÷ 0300 × 0308 ÷ 0020 ÷	
       |÷ 0300 ÷ 000D ÷	
       |÷ 0300 × 0308 ÷ 000D ÷	
       |÷ 0300 ÷ 000A ÷	
       |÷ 0300 × 0308 ÷ 000A ÷	
       |÷ 0300 ÷ 0001 ÷	
       |÷ 0300 × 0308 ÷ 0001 ÷	
       |÷ 0300 × 0300 ÷	
       |÷ 0300 × 0308 × 0300 ÷	
       |÷ 0300 ÷ 0600 ÷	
       |÷ 0300 × 0308 ÷ 0600 ÷	
       |÷ 0300 × 0903 ÷	
       |÷ 0300 × 0308 × 0903 ÷	
       |÷ 0300 ÷ 1100 ÷	
       |÷ 0300 × 0308 ÷ 1100 ÷	
       |÷ 0300 ÷ 1160 ÷	
       |÷ 0300 × 0308 ÷ 1160 ÷	
       |÷ 0300 ÷ 11A8 ÷	
       |÷ 0300 × 0308 ÷ 11A8 ÷	
       |÷ 0300 ÷ AC00 ÷	
       |÷ 0300 × 0308 ÷ AC00 ÷	
       |÷ 0300 ÷ AC01 ÷	
       |÷ 0300 × 0308 ÷ AC01 ÷	
       |÷ 0300 ÷ 1F1E6 ÷	
       |÷ 0300 × 0308 ÷ 1F1E6 ÷	
       |÷ 0300 ÷ 261D ÷	
       |÷ 0300 × 0308 ÷ 261D ÷	
       |÷ 0300 ÷ 1F3FB ÷	
       |÷ 0300 × 0308 ÷ 1F3FB ÷	
       |÷ 0300 × 200D ÷	
       |÷ 0300 × 0308 × 200D ÷	
       |÷ 0300 ÷ 2640 ÷	
       |÷ 0300 × 0308 ÷ 2640 ÷	
       |÷ 0300 ÷ 1F466 ÷	
       |÷ 0300 × 0308 ÷ 1F466 ÷	
       |÷ 0300 ÷ 0378 ÷	
       |÷ 0300 × 0308 ÷ 0378 ÷	
       |÷ 0300 ÷ D800 ÷	
       |÷ 0300 × 0308 ÷ D800 ÷	
       |÷ 0600 × 0020 ÷	
       |÷ 0600 × 0308 ÷ 0020 ÷	
       |÷ 0600 ÷ 000D ÷	
       |÷ 0600 × 0308 ÷ 000D ÷	
       |÷ 0600 ÷ 000A ÷	
       |÷ 0600 × 0308 ÷ 000A ÷	
       |÷ 0600 ÷ 0001 ÷	
       |÷ 0600 × 0308 ÷ 0001 ÷	
       |÷ 0600 × 0300 ÷	
       |÷ 0600 × 0308 × 0300 ÷	
       |÷ 0600 × 0600 ÷	
       |÷ 0600 × 0308 ÷ 0600 ÷	
       |÷ 0600 × 0903 ÷	
       |÷ 0600 × 0308 × 0903 ÷	
       |÷ 0600 × 1100 ÷	
       |÷ 0600 × 0308 ÷ 1100 ÷	
       |÷ 0600 × 1160 ÷	
       |÷ 0600 × 0308 ÷ 1160 ÷	
       |÷ 0600 × 11A8 ÷	
       |÷ 0600 × 0308 ÷ 11A8 ÷	
       |÷ 0600 × AC00 ÷	
       |÷ 0600 × 0308 ÷ AC00 ÷	
       |÷ 0600 × AC01 ÷	
       |÷ 0600 × 0308 ÷ AC01 ÷	
       |÷ 0600 × 1F1E6 ÷	
       |÷ 0600 × 0308 ÷ 1F1E6 ÷	
       |÷ 0600 × 261D ÷	
       |÷ 0600 × 0308 ÷ 261D ÷	
       |÷ 0600 × 1F3FB ÷	
       |÷ 0600 × 0308 ÷ 1F3FB ÷	
       |÷ 0600 × 200D ÷	
       |÷ 0600 × 0308 × 200D ÷	
       |÷ 0600 × 2640 ÷	
       |÷ 0600 × 0308 ÷ 2640 ÷	
       |÷ 0600 × 1F466 ÷	
       |÷ 0600 × 0308 ÷ 1F466 ÷	
       |÷ 0600 × 0378 ÷	
       |÷ 0600 × 0308 ÷ 0378 ÷	
       |÷ 0600 ÷ D800 ÷	
       |÷ 0600 × 0308 ÷ D800 ÷	
       |÷ 0903 ÷ 0020 ÷	
       |÷ 0903 × 0308 ÷ 0020 ÷	
       |÷ 0903 ÷ 000D ÷	
       |÷ 0903 × 0308 ÷ 000D ÷	
       |÷ 0903 ÷ 000A ÷	
       |÷ 0903 × 0308 ÷ 000A ÷	
       |÷ 0903 ÷ 0001 ÷	
       |÷ 0903 × 0308 ÷ 0001 ÷	
       |÷ 0903 × 0300 ÷	
       |÷ 0903 × 0308 × 0300 ÷	
       |÷ 0903 ÷ 0600 ÷	
       |÷ 0903 × 0308 ÷ 0600 ÷	
       |÷ 0903 × 0903 ÷	
       |÷ 0903 × 0308 × 0903 ÷	
       |÷ 0903 ÷ 1100 ÷	
       |÷ 0903 × 0308 ÷ 1100 ÷	
       |÷ 0903 ÷ 1160 ÷	
       |÷ 0903 × 0308 ÷ 1160 ÷	
       |÷ 0903 ÷ 11A8 ÷	
       |÷ 0903 × 0308 ÷ 11A8 ÷	
       |÷ 0903 ÷ AC00 ÷	
       |÷ 0903 × 0308 ÷ AC00 ÷	
       |÷ 0903 ÷ AC01 ÷	
       |÷ 0903 × 0308 ÷ AC01 ÷	
       |÷ 0903 ÷ 1F1E6 ÷	
       |÷ 0903 × 0308 ÷ 1F1E6 ÷	
       |÷ 0903 ÷ 261D ÷	
       |÷ 0903 × 0308 ÷ 261D ÷	
       |÷ 0903 ÷ 1F3FB ÷	
       |÷ 0903 × 0308 ÷ 1F3FB ÷	
       |÷ 0903 × 200D ÷	
       |÷ 0903 × 0308 × 200D ÷	
       |÷ 0903 ÷ 2640 ÷	
       |÷ 0903 × 0308 ÷ 2640 ÷	
       |÷ 0903 ÷ 1F466 ÷	
       |÷ 0903 × 0308 ÷ 1F466 ÷	
       |÷ 0903 ÷ 0378 ÷	
       |÷ 0903 × 0308 ÷ 0378 ÷	
       |÷ 0903 ÷ D800 ÷	
       |÷ 0903 × 0308 ÷ D800 ÷	
       |÷ 1100 ÷ 0020 ÷	
       |÷ 1100 × 0308 ÷ 0020 ÷	
       |÷ 1100 ÷ 000D ÷	
       |÷ 1100 × 0308 ÷ 000D ÷	
       |÷ 1100 ÷ 000A ÷	
       |÷ 1100 × 0308 ÷ 000A ÷	
       |÷ 1100 ÷ 0001 ÷	
       |÷ 1100 × 0308 ÷ 0001 ÷	
       |÷ 1100 × 0300 ÷	
       |÷ 1100 × 0308 × 0300 ÷	
       |÷ 1100 ÷ 0600 ÷	
       |÷ 1100 × 0308 ÷ 0600 ÷	
       |÷ 1100 × 0903 ÷	
       |÷ 1100 × 0308 × 0903 ÷	
       |÷ 1100 × 1100 ÷	
       |÷ 1100 × 0308 ÷ 1100 ÷	
       |÷ 1100 × 1160 ÷	
       |÷ 1100 × 0308 ÷ 1160 ÷	
       |÷ 1100 ÷ 11A8 ÷	
       |÷ 1100 × 0308 ÷ 11A8 ÷	
       |÷ 1100 × AC00 ÷	
       |÷ 1100 × 0308 ÷ AC00 ÷	
       |÷ 1100 × AC01 ÷	
       |÷ 1100 × 0308 ÷ AC01 ÷	
       |÷ 1100 ÷ 1F1E6 ÷	
       |÷ 1100 × 0308 ÷ 1F1E6 ÷	
       |÷ 1100 ÷ 261D ÷	
       |÷ 1100 × 0308 ÷ 261D ÷	
       |÷ 1100 ÷ 1F3FB ÷	
       |÷ 1100 × 0308 ÷ 1F3FB ÷	
       |÷ 1100 × 200D ÷	
       |÷ 1100 × 0308 × 200D ÷	
       |÷ 1100 ÷ 2640 ÷	
       |÷ 1100 × 0308 ÷ 2640 ÷	
       |÷ 1100 ÷ 1F466 ÷	
       |÷ 1100 × 0308 ÷ 1F466 ÷	
       |÷ 1100 ÷ 0378 ÷	
       |÷ 1100 × 0308 ÷ 0378 ÷	
       |÷ 1100 ÷ D800 ÷	
       |÷ 1100 × 0308 ÷ D800 ÷	
       |÷ 1160 ÷ 0020 ÷	
       |÷ 1160 × 0308 ÷ 0020 ÷	
       |÷ 1160 ÷ 000D ÷	
       |÷ 1160 × 0308 ÷ 000D ÷	
       |÷ 1160 ÷ 000A ÷	
       |÷ 1160 × 0308 ÷ 000A ÷	
       |÷ 1160 ÷ 0001 ÷	
       |÷ 1160 × 0308 ÷ 0001 ÷	
       |÷ 1160 × 0300 ÷	
       |÷ 1160 × 0308 × 0300 ÷	
       |÷ 1160 ÷ 0600 ÷	
       |÷ 1160 × 0308 ÷ 0600 ÷	
       |÷ 1160 × 0903 ÷	
       |÷ 1160 × 0308 × 0903 ÷	
       |÷ 1160 ÷ 1100 ÷	
       |÷ 1160 × 0308 ÷ 1100 ÷	
       |÷ 1160 × 1160 ÷	
       |÷ 1160 × 0308 ÷ 1160 ÷	
       |÷ 1160 × 11A8 ÷	
       |÷ 1160 × 0308 ÷ 11A8 ÷	
       |÷ 1160 ÷ AC00 ÷	
       |÷ 1160 × 0308 ÷ AC00 ÷	
       |÷ 1160 ÷ AC01 ÷	
       |÷ 1160 × 0308 ÷ AC01 ÷	
       |÷ 1160 ÷ 1F1E6 ÷	
       |÷ 1160 × 0308 ÷ 1F1E6 ÷	
       |÷ 1160 ÷ 261D ÷	
       |÷ 1160 × 0308 ÷ 261D ÷	
       |÷ 1160 ÷ 1F3FB ÷	
       |÷ 1160 × 0308 ÷ 1F3FB ÷	
       |÷ 1160 × 200D ÷	
       |÷ 1160 × 0308 × 200D ÷	
       |÷ 1160 ÷ 2640 ÷	
       |÷ 1160 × 0308 ÷ 2640 ÷	
       |÷ 1160 ÷ 1F466 ÷	
       |÷ 1160 × 0308 ÷ 1F466 ÷	
       |÷ 1160 ÷ 0378 ÷	
       |÷ 1160 × 0308 ÷ 0378 ÷	
       |÷ 1160 ÷ D800 ÷	
       |÷ 1160 × 0308 ÷ D800 ÷	
       |÷ 11A8 ÷ 0020 ÷	
       |÷ 11A8 × 0308 ÷ 0020 ÷	
       |÷ 11A8 ÷ 000D ÷	
       |÷ 11A8 × 0308 ÷ 000D ÷	
       |÷ 11A8 ÷ 000A ÷	
       |÷ 11A8 × 0308 ÷ 000A ÷	
       |÷ 11A8 ÷ 0001 ÷	
       |÷ 11A8 × 0308 ÷ 0001 ÷	
       |÷ 11A8 × 0300 ÷	
       |÷ 11A8 × 0308 × 0300 ÷	
       |÷ 11A8 ÷ 0600 ÷	
       |÷ 11A8 × 0308 ÷ 0600 ÷	
       |÷ 11A8 × 0903 ÷	
       |÷ 11A8 × 0308 × 0903 ÷	
       |÷ 11A8 ÷ 1100 ÷	
       |÷ 11A8 × 0308 ÷ 1100 ÷	
       |÷ 11A8 ÷ 1160 ÷	
       |÷ 11A8 × 0308 ÷ 1160 ÷	
       |÷ 11A8 × 11A8 ÷	
       |÷ 11A8 × 0308 ÷ 11A8 ÷	
       |÷ 11A8 ÷ AC00 ÷	
       |÷ 11A8 × 0308 ÷ AC00 ÷	
       |÷ 11A8 ÷ AC01 ÷	
       |÷ 11A8 × 0308 ÷ AC01 ÷	
       |÷ 11A8 ÷ 1F1E6 ÷	
       |÷ 11A8 × 0308 ÷ 1F1E6 ÷	
       |÷ 11A8 ÷ 261D ÷	
       |÷ 11A8 × 0308 ÷ 261D ÷	
       |÷ 11A8 ÷ 1F3FB ÷	
       |÷ 11A8 × 0308 ÷ 1F3FB ÷	
       |÷ 11A8 × 200D ÷	
       |÷ 11A8 × 0308 × 200D ÷	
       |÷ 11A8 ÷ 2640 ÷	
       |÷ 11A8 × 0308 ÷ 2640 ÷	
       |÷ 11A8 ÷ 1F466 ÷	
       |÷ 11A8 × 0308 ÷ 1F466 ÷	
       |÷ 11A8 ÷ 0378 ÷	
       |÷ 11A8 × 0308 ÷ 0378 ÷	
       |÷ 11A8 ÷ D800 ÷	
       |÷ 11A8 × 0308 ÷ D800 ÷	
       |÷ AC00 ÷ 0020 ÷	
       |÷ AC00 × 0308 ÷ 0020 ÷	
       |÷ AC00 ÷ 000D ÷	
       |÷ AC00 × 0308 ÷ 000D ÷	
       |÷ AC00 ÷ 000A ÷	
       |÷ AC00 × 0308 ÷ 000A ÷	
       |÷ AC00 ÷ 0001 ÷	
       |÷ AC00 × 0308 ÷ 0001 ÷	
       |÷ AC00 × 0300 ÷	
       |÷ AC00 × 0308 × 0300 ÷	
       |÷ AC00 ÷ 0600 ÷	
       |÷ AC00 × 0308 ÷ 0600 ÷	
       |÷ AC00 × 0903 ÷	
       |÷ AC00 × 0308 × 0903 ÷	
       |÷ AC00 ÷ 1100 ÷	
       |÷ AC00 × 0308 ÷ 1100 ÷	
       |÷ AC00 × 1160 ÷	
       |÷ AC00 × 0308 ÷ 1160 ÷	
       |÷ AC00 × 11A8 ÷	
       |÷ AC00 × 0308 ÷ 11A8 ÷	
       |÷ AC00 ÷ AC00 ÷	
       |÷ AC00 × 0308 ÷ AC00 ÷	
       |÷ AC00 ÷ AC01 ÷	
       |÷ AC00 × 0308 ÷ AC01 ÷	
       |÷ AC00 ÷ 1F1E6 ÷	
       |÷ AC00 × 0308 ÷ 1F1E6 ÷	
       |÷ AC00 ÷ 261D ÷	
       |÷ AC00 × 0308 ÷ 261D ÷	
       |÷ AC00 ÷ 1F3FB ÷	
       |÷ AC00 × 0308 ÷ 1F3FB ÷	
       |÷ AC00 × 200D ÷	
       |÷ AC00 × 0308 × 200D ÷	
       |÷ AC00 ÷ 2640 ÷	
       |÷ AC00 × 0308 ÷ 2640 ÷	
       |÷ AC00 ÷ 1F466 ÷	
       |÷ AC00 × 0308 ÷ 1F466 ÷	
       |÷ AC00 ÷ 0378 ÷	
       |÷ AC00 × 0308 ÷ 0378 ÷	
       |÷ AC00 ÷ D800 ÷	
       |÷ AC00 × 0308 ÷ D800 ÷	
       |÷ AC01 ÷ 0020 ÷	
       |÷ AC01 × 0308 ÷ 0020 ÷	
       |÷ AC01 ÷ 000D ÷	
       |÷ AC01 × 0308 ÷ 000D ÷	
       |÷ AC01 ÷ 000A ÷	
       |÷ AC01 × 0308 ÷ 000A ÷	
       |÷ AC01 ÷ 0001 ÷	
       |÷ AC01 × 0308 ÷ 0001 ÷	
       |÷ AC01 × 0300 ÷	
       |÷ AC01 × 0308 × 0300 ÷	
       |÷ AC01 ÷ 0600 ÷	
       |÷ AC01 × 0308 ÷ 0600 ÷	
       |÷ AC01 × 0903 ÷	
       |÷ AC01 × 0308 × 0903 ÷	
       |÷ AC01 ÷ 1100 ÷	
       |÷ AC01 × 0308 ÷ 1100 ÷	
       |÷ AC01 ÷ 1160 ÷	
       |÷ AC01 × 0308 ÷ 1160 ÷	
       |÷ AC01 × 11A8 ÷	
       |÷ AC01 × 0308 ÷ 11A8 ÷	
       |÷ AC01 ÷ AC00 ÷	
       |÷ AC01 × 0308 ÷ AC00 ÷	
       |÷ AC01 ÷ AC01 ÷	
       |÷ AC01 × 0308 ÷ AC01 ÷	
       |÷ AC01 ÷ 1F1E6 ÷	
       |÷ AC01 × 0308 ÷ 1F1E6 ÷	
       |÷ AC01 ÷ 261D ÷	
       |÷ AC01 × 0308 ÷ 261D ÷	
       |÷ AC01 ÷ 1F3FB ÷	
       |÷ AC01 × 0308 ÷ 1F3FB ÷	
       |÷ AC01 × 200D ÷	
       |÷ AC01 × 0308 × 200D ÷	
       |÷ AC01 ÷ 2640 ÷	
       |÷ AC01 × 0308 ÷ 2640 ÷	
       |÷ AC01 ÷ 1F466 ÷	
       |÷ AC01 × 0308 ÷ 1F466 ÷	
       |÷ AC01 ÷ 0378 ÷	
       |÷ AC01 × 0308 ÷ 0378 ÷	
       |÷ AC01 ÷ D800 ÷	
       |÷ AC01 × 0308 ÷ D800 ÷	
       |÷ 1F1E6 ÷ 0020 ÷	
       |÷ 1F1E6 × 0308 ÷ 0020 ÷	
       |÷ 1F1E6 ÷ 000D ÷	
       |÷ 1F1E6 × 0308 ÷ 000D ÷	
       |÷ 1F1E6 ÷ 000A ÷	
       |÷ 1F1E6 × 0308 ÷ 000A ÷	
       |÷ 1F1E6 ÷ 0001 ÷	
       |÷ 1F1E6 × 0308 ÷ 0001 ÷	
       |÷ 1F1E6 × 0300 ÷	
       |÷ 1F1E6 × 0308 × 0300 ÷	
       |÷ 1F1E6 ÷ 0600 ÷	
       |÷ 1F1E6 × 0308 ÷ 0600 ÷	
       |÷ 1F1E6 × 0903 ÷	
       |÷ 1F1E6 × 0308 × 0903 ÷	
       |÷ 1F1E6 ÷ 1100 ÷	
       |÷ 1F1E6 × 0308 ÷ 1100 ÷	
       |÷ 1F1E6 ÷ 1160 ÷	
       |÷ 1F1E6 × 0308 ÷ 1160 ÷	
       |÷ 1F1E6 ÷ 11A8 ÷	
       |÷ 1F1E6 × 0308 ÷ 11A8 ÷	
       |÷ 1F1E6 ÷ AC00 ÷	
       |÷ 1F1E6 × 0308 ÷ AC00 ÷	
       |÷ 1F1E6 ÷ AC01 ÷	
       |÷ 1F1E6 × 0308 ÷ AC01 ÷	
       |÷ 1F1E6 × 1F1E6 ÷	
       |÷ 1F1E6 × 0308 ÷ 1F1E6 ÷	
       |÷ 1F1E6 ÷ 261D ÷	
       |÷ 1F1E6 × 0308 ÷ 261D ÷	
       |÷ 1F1E6 ÷ 1F3FB ÷	
       |÷ 1F1E6 × 0308 ÷ 1F3FB ÷	
       |÷ 1F1E6 × 200D ÷	
       |÷ 1F1E6 × 0308 × 200D ÷	
       |÷ 1F1E6 ÷ 2640 ÷	
       |÷ 1F1E6 × 0308 ÷ 2640 ÷	
       |÷ 1F1E6 ÷ 1F466 ÷	
       |÷ 1F1E6 × 0308 ÷ 1F466 ÷	
       |÷ 1F1E6 ÷ 0378 ÷	
       |÷ 1F1E6 × 0308 ÷ 0378 ÷	
       |÷ 1F1E6 ÷ D800 ÷	
       |÷ 1F1E6 × 0308 ÷ D800 ÷	
       |÷ 261D ÷ 0020 ÷	
       |÷ 261D × 0308 ÷ 0020 ÷	
       |÷ 261D ÷ 000D ÷	
       |÷ 261D × 0308 ÷ 000D ÷	
       |÷ 261D ÷ 000A ÷	
       |÷ 261D × 0308 ÷ 000A ÷	
       |÷ 261D ÷ 0001 ÷	
       |÷ 261D × 0308 ÷ 0001 ÷	
       |÷ 261D × 0300 ÷	
       |÷ 261D × 0308 × 0300 ÷	
       |÷ 261D ÷ 0600 ÷	
       |÷ 261D × 0308 ÷ 0600 ÷	
       |÷ 261D × 0903 ÷	
       |÷ 261D × 0308 × 0903 ÷	
       |÷ 261D ÷ 1100 ÷	
       |÷ 261D × 0308 ÷ 1100 ÷	
       |÷ 261D ÷ 1160 ÷	
       |÷ 261D × 0308 ÷ 1160 ÷	
       |÷ 261D ÷ 11A8 ÷	
       |÷ 261D × 0308 ÷ 11A8 ÷	
       |÷ 261D ÷ AC00 ÷	
       |÷ 261D × 0308 ÷ AC00 ÷	
       |÷ 261D ÷ AC01 ÷	
       |÷ 261D × 0308 ÷ AC01 ÷	
       |÷ 261D ÷ 1F1E6 ÷	
       |÷ 261D × 0308 ÷ 1F1E6 ÷	
       |÷ 261D ÷ 261D ÷	
       |÷ 261D × 0308 ÷ 261D ÷	
       |÷ 261D × 1F3FB ÷	
       |÷ 261D × 0308 × 1F3FB ÷	
       |÷ 261D × 200D ÷	
       |÷ 261D × 0308 × 200D ÷	
       |÷ 261D ÷ 2640 ÷	
       |÷ 261D × 0308 ÷ 2640 ÷	
       |÷ 261D ÷ 1F466 ÷	
       |÷ 261D × 0308 ÷ 1F466 ÷	
       |÷ 261D ÷ 0378 ÷	
       |÷ 261D × 0308 ÷ 0378 ÷	
       |÷ 261D ÷ D800 ÷	
       |÷ 261D × 0308 ÷ D800 ÷	
       |÷ 1F3FB ÷ 0020 ÷	
       |÷ 1F3FB × 0308 ÷ 0020 ÷	
       |÷ 1F3FB ÷ 000D ÷	
       |÷ 1F3FB × 0308 ÷ 000D ÷	
       |÷ 1F3FB ÷ 000A ÷	
       |÷ 1F3FB × 0308 ÷ 000A ÷	
       |÷ 1F3FB ÷ 0001 ÷	
       |÷ 1F3FB × 0308 ÷ 0001 ÷	
       |÷ 1F3FB × 0300 ÷	
       |÷ 1F3FB × 0308 × 0300 ÷	
       |÷ 1F3FB ÷ 0600 ÷	
       |÷ 1F3FB × 0308 ÷ 0600 ÷	
       |÷ 1F3FB × 0903 ÷	
       |÷ 1F3FB × 0308 × 0903 ÷	
       |÷ 1F3FB ÷ 1100 ÷	
       |÷ 1F3FB × 0308 ÷ 1100 ÷	
       |÷ 1F3FB ÷ 1160 ÷	
       |÷ 1F3FB × 0308 ÷ 1160 ÷	
       |÷ 1F3FB ÷ 11A8 ÷	
       |÷ 1F3FB × 0308 ÷ 11A8 ÷	
       |÷ 1F3FB ÷ AC00 ÷	
       |÷ 1F3FB × 0308 ÷ AC00 ÷	
       |÷ 1F3FB ÷ AC01 ÷	
       |÷ 1F3FB × 0308 ÷ AC01 ÷	
       |÷ 1F3FB ÷ 1F1E6 ÷	
       |÷ 1F3FB × 0308 ÷ 1F1E6 ÷	
       |÷ 1F3FB ÷ 261D ÷	
       |÷ 1F3FB × 0308 ÷ 261D ÷	
       |÷ 1F3FB ÷ 1F3FB ÷	
       |÷ 1F3FB × 0308 ÷ 1F3FB ÷	
       |÷ 1F3FB × 200D ÷	
       |÷ 1F3FB × 0308 × 200D ÷	
       |÷ 1F3FB ÷ 2640 ÷	
       |÷ 1F3FB × 0308 ÷ 2640 ÷	
       |÷ 1F3FB ÷ 1F466 ÷	
       |÷ 1F3FB × 0308 ÷ 1F466 ÷	
       |÷ 1F3FB ÷ 0378 ÷	
       |÷ 1F3FB × 0308 ÷ 0378 ÷	
       |÷ 1F3FB ÷ D800 ÷	
       |÷ 1F3FB × 0308 ÷ D800 ÷	
       |÷ 200D ÷ 0020 ÷	
       |÷ 200D × 0308 ÷ 0020 ÷	
       |÷ 200D ÷ 000D ÷	
       |÷ 200D × 0308 ÷ 000D ÷	
       |÷ 200D ÷ 000A ÷	
       |÷ 200D × 0308 ÷ 000A ÷	
       |÷ 200D ÷ 0001 ÷	
       |÷ 200D × 0308 ÷ 0001 ÷	
       |÷ 200D × 0300 ÷	
       |÷ 200D × 0308 × 0300 ÷	
       |÷ 200D ÷ 0600 ÷	
       |÷ 200D × 0308 ÷ 0600 ÷	
       |÷ 200D × 0903 ÷	
       |÷ 200D × 0308 × 0903 ÷	
       |÷ 200D ÷ 1100 ÷	
       |÷ 200D × 0308 ÷ 1100 ÷	
       |÷ 200D ÷ 1160 ÷	
       |÷ 200D × 0308 ÷ 1160 ÷	
       |÷ 200D ÷ 11A8 ÷	
       |÷ 200D × 0308 ÷ 11A8 ÷	
       |÷ 200D ÷ AC00 ÷	
       |÷ 200D × 0308 ÷ AC00 ÷	
       |÷ 200D ÷ AC01 ÷	
       |÷ 200D × 0308 ÷ AC01 ÷	
       |÷ 200D ÷ 1F1E6 ÷	
       |÷ 200D × 0308 ÷ 1F1E6 ÷	
       |÷ 200D ÷ 261D ÷	
       |÷ 200D × 0308 ÷ 261D ÷	
       |÷ 200D ÷ 1F3FB ÷	
       |÷ 200D × 0308 ÷ 1F3FB ÷	
       |÷ 200D × 200D ÷	
       |÷ 200D × 0308 × 200D ÷	
       |÷ 200D × 2640 ÷	
       |÷ 200D × 0308 ÷ 2640 ÷	
       |÷ 200D × 1F466 ÷	
       |÷ 200D × 0308 ÷ 1F466 ÷	
       |÷ 200D ÷ 0378 ÷	
       |÷ 200D × 0308 ÷ 0378 ÷	
       |÷ 200D ÷ D800 ÷	
       |÷ 200D × 0308 ÷ D800 ÷	
       |÷ 2640 ÷ 0020 ÷	
       |÷ 2640 × 0308 ÷ 0020 ÷	
       |÷ 2640 ÷ 000D ÷	
       |÷ 2640 × 0308 ÷ 000D ÷	
       |÷ 2640 ÷ 000A ÷	
       |÷ 2640 × 0308 ÷ 000A ÷	
       |÷ 2640 ÷ 0001 ÷	
       |÷ 2640 × 0308 ÷ 0001 ÷	
       |÷ 2640 × 0300 ÷	
       |÷ 2640 × 0308 × 0300 ÷	
       |÷ 2640 ÷ 0600 ÷	
       |÷ 2640 × 0308 ÷ 0600 ÷	
       |÷ 2640 × 0903 ÷	
       |÷ 2640 × 0308 × 0903 ÷	
       |÷ 2640 ÷ 1100 ÷	
       |÷ 2640 × 0308 ÷ 1100 ÷	
       |÷ 2640 ÷ 1160 ÷	
       |÷ 2640 × 0308 ÷ 1160 ÷	
       |÷ 2640 ÷ 11A8 ÷	
       |÷ 2640 × 0308 ÷ 11A8 ÷	
       |÷ 2640 ÷ AC00 ÷	
       |÷ 2640 × 0308 ÷ AC00 ÷	
       |÷ 2640 ÷ AC01 ÷	
       |÷ 2640 × 0308 ÷ AC01 ÷	
       |÷ 2640 ÷ 1F1E6 ÷	
       |÷ 2640 × 0308 ÷ 1F1E6 ÷	
       |÷ 2640 ÷ 261D ÷	
       |÷ 2640 × 0308 ÷ 261D ÷	
       |÷ 2640 ÷ 1F3FB ÷	
       |÷ 2640 × 0308 ÷ 1F3FB ÷	
       |÷ 2640 × 200D ÷	
       |÷ 2640 × 0308 × 200D ÷	
       |÷ 2640 ÷ 2640 ÷	
       |÷ 2640 × 0308 ÷ 2640 ÷	
       |÷ 2640 ÷ 1F466 ÷	
       |÷ 2640 × 0308 ÷ 1F466 ÷	
       |÷ 2640 ÷ 0378 ÷	
       |÷ 2640 × 0308 ÷ 0378 ÷	
       |÷ 2640 ÷ D800 ÷	
       |÷ 2640 × 0308 ÷ D800 ÷	
       |÷ 1F466 ÷ 0020 ÷	
       |÷ 1F466 × 0308 ÷ 0020 ÷	
       |÷ 1F466 ÷ 000D ÷	
       |÷ 1F466 × 0308 ÷ 000D ÷	
       |÷ 1F466 ÷ 000A ÷	
       |÷ 1F466 × 0308 ÷ 000A ÷	
       |÷ 1F466 ÷ 0001 ÷	
       |÷ 1F466 × 0308 ÷ 0001 ÷	
       |÷ 1F466 × 0300 ÷	
       |÷ 1F466 × 0308 × 0300 ÷	
       |÷ 1F466 ÷ 0600 ÷	
       |÷ 1F466 × 0308 ÷ 0600 ÷	
       |÷ 1F466 × 0903 ÷	
       |÷ 1F466 × 0308 × 0903 ÷	
       |÷ 1F466 ÷ 1100 ÷	
       |÷ 1F466 × 0308 ÷ 1100 ÷	
       |÷ 1F466 ÷ 1160 ÷	
       |÷ 1F466 × 0308 ÷ 1160 ÷	
       |÷ 1F466 ÷ 11A8 ÷	
       |÷ 1F466 × 0308 ÷ 11A8 ÷	
       |÷ 1F466 ÷ AC00 ÷	
       |÷ 1F466 × 0308 ÷ AC00 ÷	
       |÷ 1F466 ÷ AC01 ÷	
       |÷ 1F466 × 0308 ÷ AC01 ÷	
       |÷ 1F466 ÷ 1F1E6 ÷	
       |÷ 1F466 × 0308 ÷ 1F1E6 ÷	
       |÷ 1F466 ÷ 261D ÷	
       |÷ 1F466 × 0308 ÷ 261D ÷	
       |÷ 1F466 × 1F3FB ÷	
       |÷ 1F466 × 0308 × 1F3FB ÷	
       |÷ 1F466 × 200D ÷	
       |÷ 1F466 × 0308 × 200D ÷	
       |÷ 1F466 ÷ 2640 ÷	
       |÷ 1F466 × 0308 ÷ 2640 ÷	
       |÷ 1F466 ÷ 1F466 ÷	
       |÷ 1F466 × 0308 ÷ 1F466 ÷	
       |÷ 1F466 ÷ 0378 ÷	
       |÷ 1F466 × 0308 ÷ 0378 ÷	
       |÷ 1F466 ÷ D800 ÷	
       |÷ 1F466 × 0308 ÷ D800 ÷	
       |÷ 0378 ÷ 0020 ÷	
       |÷ 0378 × 0308 ÷ 0020 ÷	
       |÷ 0378 ÷ 000D ÷	
       |÷ 0378 × 0308 ÷ 000D ÷	
       |÷ 0378 ÷ 000A ÷	
       |÷ 0378 × 0308 ÷ 000A ÷	
       |÷ 0378 ÷ 0001 ÷	
       |÷ 0378 × 0308 ÷ 0001 ÷	
       |÷ 0378 × 0300 ÷	
       |÷ 0378 × 0308 × 0300 ÷	
       |÷ 0378 ÷ 0600 ÷	
       |÷ 0378 × 0308 ÷ 0600 ÷	
       |÷ 0378 × 0903 ÷	
       |÷ 0378 × 0308 × 0903 ÷	
       |÷ 0378 ÷ 1100 ÷	
       |÷ 0378 × 0308 ÷ 1100 ÷	
       |÷ 0378 ÷ 1160 ÷	
       |÷ 0378 × 0308 ÷ 1160 ÷	
       |÷ 0378 ÷ 11A8 ÷	
       |÷ 0378 × 0308 ÷ 11A8 ÷	
       |÷ 0378 ÷ AC00 ÷	
       |÷ 0378 × 0308 ÷ AC00 ÷	
       |÷ 0378 ÷ AC01 ÷	
       |÷ 0378 × 0308 ÷ AC01 ÷	
       |÷ 0378 ÷ 1F1E6 ÷	
       |÷ 0378 × 0308 ÷ 1F1E6 ÷	
       |÷ 0378 ÷ 261D ÷	
       |÷ 0378 × 0308 ÷ 261D ÷	
       |÷ 0378 ÷ 1F3FB ÷	
       |÷ 0378 × 0308 ÷ 1F3FB ÷	
       |÷ 0378 × 200D ÷	
       |÷ 0378 × 0308 × 200D ÷	
       |÷ 0378 ÷ 2640 ÷	
       |÷ 0378 × 0308 ÷ 2640 ÷	
       |÷ 0378 ÷ 1F466 ÷	
       |÷ 0378 × 0308 ÷ 1F466 ÷	
       |÷ 0378 ÷ 0378 ÷	
       |÷ 0378 × 0308 ÷ 0378 ÷	
       |÷ 0378 ÷ D800 ÷	
       |÷ 0378 × 0308 ÷ D800 ÷	
       |÷ D800 ÷ 0020 ÷	
       |÷ D800 ÷ 0308 ÷ 0020 ÷	
       |÷ D800 ÷ 000D ÷	
       |÷ D800 ÷ 0308 ÷ 000D ÷	
       |÷ D800 ÷ 000A ÷	
       |÷ D800 ÷ 0308 ÷ 000A ÷	
       |÷ D800 ÷ 0001 ÷	
       |÷ D800 ÷ 0308 ÷ 0001 ÷	
       |÷ D800 ÷ 0300 ÷	
       |÷ D800 ÷ 0308 × 0300 ÷	
       |÷ D800 ÷ 0600 ÷	
       |÷ D800 ÷ 0308 ÷ 0600 ÷	
       |÷ D800 ÷ 0903 ÷	
       |÷ D800 ÷ 0308 × 0903 ÷	
       |÷ D800 ÷ 1100 ÷	
       |÷ D800 ÷ 0308 ÷ 1100 ÷	
       |÷ D800 ÷ 1160 ÷	
       |÷ D800 ÷ 0308 ÷ 1160 ÷	
       |÷ D800 ÷ 11A8 ÷	
       |÷ D800 ÷ 0308 ÷ 11A8 ÷	
       |÷ D800 ÷ AC00 ÷	
       |÷ D800 ÷ 0308 ÷ AC00 ÷	
       |÷ D800 ÷ AC01 ÷	
       |÷ D800 ÷ 0308 ÷ AC01 ÷	
       |÷ D800 ÷ 1F1E6 ÷	
       |÷ D800 ÷ 0308 ÷ 1F1E6 ÷	
       |÷ D800 ÷ 261D ÷	
       |÷ D800 ÷ 0308 ÷ 261D ÷	
       |÷ D800 ÷ 1F3FB ÷	
       |÷ D800 ÷ 0308 ÷ 1F3FB ÷	
       |÷ D800 ÷ 200D ÷	
       |÷ D800 ÷ 0308 × 200D ÷	
       |÷ D800 ÷ 2640 ÷	
       |÷ D800 ÷ 0308 ÷ 2640 ÷	
       |÷ D800 ÷ 1F466 ÷	
       |÷ D800 ÷ 0308 ÷ 1F466 ÷	
       |÷ D800 ÷ 0378 ÷	
       |÷ D800 ÷ 0308 ÷ 0378 ÷	
       |÷ D800 ÷ D800 ÷	
       |÷ D800 ÷ 0308 ÷ D800 ÷	
       |÷ 000D × 000A ÷ 0061 ÷ 000A ÷ 0308 ÷	
       |÷ 0061 × 0308 ÷	
       |÷ 0020 × 200D ÷ 0646 ÷	
       |÷ 0646 × 200D ÷ 0020 ÷	
       |÷ 1100 × 1100 ÷	
       |÷ AC00 × 11A8 ÷ 1100 ÷	
       |÷ AC01 × 11A8 ÷ 1100 ÷	
       |÷ 1F1E6 × 1F1E7 ÷ 1F1E8 ÷ 0062 ÷	
       |÷ 0061 ÷ 1F1E6 × 1F1E7 ÷ 1F1E8 ÷ 0062 ÷	
       |÷ 0061 ÷ 1F1E6 × 1F1E7 × 200D ÷ 1F1E8 ÷ 0062 ÷	
       |÷ 0061 ÷ 1F1E6 × 200D ÷ 1F1E7 × 1F1E8 ÷ 0062 ÷	
       |÷ 0061 ÷ 1F1E6 × 1F1E7 ÷ 1F1E8 × 1F1E9 ÷ 0062 ÷	
       |÷ 0061 × 200D ÷	
       |÷ 0061 × 0308 ÷ 0062 ÷	
       |÷ 0061 × 0903 ÷ 0062 ÷	
       |÷ 0061 ÷ 0600 × 0062 ÷	
       |÷ 261D × 1F3FB ÷ 261D ÷	
       |÷ 1F466 × 1F3FB ÷	
       |÷ 200D × 1F466 × 1F3FB ÷	
       |÷ 200D × 2640 ÷	
       |÷ 200D × 1F466 ÷	
       |÷ 1F466 ÷ 1F466 ÷	
     """.stripMargin
}
