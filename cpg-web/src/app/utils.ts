export default class Utils {

  private static greekToGreeklishMap = [
    {find: 'ΟΥ', replace: 'OU'},
    {find: 'Ου', replace: 'Ou'},
    {find: 'ου', replace: 'ou'},
    {find: 'ΟΎ', replace: 'OU'},
    {find: 'Ού', replace: 'Ou'},
    {find: 'ού', replace: 'ou'},

    {find: 'ΓΓ', replace: 'NG'},
    {find: 'Γγ', replace: 'Ng'},
    {find: 'γγ', replace: 'ng'},
    {find: 'ΓΚ', replace: 'GK'},
    {find: 'Γκ', replace: 'Gk'},
    {find: 'γκ', replace: 'gk'},
    {find: 'ΓΞ', replace: 'NX'},
    {find: 'Γξ', replace: 'Nx'},
    {find: 'γξ', replace: 'nx'},
    {find: 'ΓΧ', replace: 'NCH'},
    {find: 'Γχ', replace: 'Nch'},
    {find: 'γχ', replace: 'nch'},
    {find: 'ΝΤ', replace: 'NT'},
    {find: 'Ντ', replace: 'Nt'},
    {find: 'ντ', replace: 'nt'},

    {find: 'Α', replace: 'A'},
    {find: 'Ά', replace: 'A'},
    {find: 'α', replace: 'a'},
    {find: 'ά', replace: 'a'},
    {find: 'Β', replace: 'V'},
    {find: 'β', replace: 'v'},
    {find: 'Γ', replace: 'G'},
    {find: 'γ', replace: 'g'},
    {find: 'Δ', replace: 'D'},
    {find: 'δ', replace: 'd'},
    {find: 'Ε', replace: 'E'},
    {find: 'Έ', replace: 'E'},
    {find: 'ε', replace: 'e'},
    {find: 'έ', replace: 'e'},
    {find: 'Ζ', replace: 'Z'},
    {find: 'ζ', replace: 'z'},
    {find: 'Η', replace: 'I'},
    {find: 'Ή', replace: 'I'},
    {find: 'η', replace: 'i'},
    {find: 'ή', replace: 'i'},
    {find: 'Θ', replace: 'Th'},
    {find: 'θ', replace: 'th'},
    {find: 'Ι', replace: 'I'},
    {find: 'Ί', replace: 'I'},
    {find: 'Ϊ', replace: 'I'},
    {find: 'ι', replace: 'i'},
    {find: 'ί', replace: 'i'},
    {find: 'ϊ', replace: 'i'},
    {find: 'ΐ', replace: 'i'},
    {find: 'Κ', replace: 'K'},
    {find: 'κ', replace: 'k'},
    {find: 'Λ', replace: 'L'},
    {find: 'λ', replace: 'l'},
    {find: 'Μ', replace: 'M'},
    {find: 'μ', replace: 'm'},
    {find: 'Ν', replace: 'N'},
    {find: 'ν', replace: 'n'},
    {find: 'Ξ', replace: 'X'},
    {find: 'ξ', replace: 'x'},
    {find: 'Ο', replace: 'O'},
    {find: 'Ό', replace: 'O'},
    {find: 'ο', replace: 'o'},
    {find: 'ό', replace: 'o'},
    {find: 'Π', replace: 'P'},
    {find: 'π', replace: 'p'},
    {find: 'Ρ', replace: 'R'},
    {find: 'ρ', replace: 'r'},
    {find: 'Σ', replace: 'S'},
    {find: 'σ', replace: 's'},
    {find: 'ς', replace: 's'},
    {find: 'Τ', replace: 'T'},
    {find: 'τ', replace: 't'},
    {find: 'Υ', replace: 'Y'},
    {find: 'Ύ', replace: 'Y'},
    {find: 'Ϋ', replace: 'Y'},
    {find: 'υ', replace: 'y'},
    {find: 'ύ', replace: 'y'},
    {find: 'ϋ', replace: 'y'},
    {find: 'ΰ', replace: 'y'},
    {find: 'Φ', replace: 'F'},
    {find: 'φ', replace: 'f'},
    {find: 'Χ', replace: 'Ch'},
    {find: 'χ', replace: 'ch'},
    {find: 'Ψ', replace: 'Ps'},
    {find: 'ψ', replace: 'ps'},
    {find: 'Ω', replace: 'O'},
    {find: 'Ώ', replace: 'O'},
    {find: 'ω', replace: 'o'},
    {find: 'ώ', replace: 'o'},
    {find: ';', replace: '?'}
  ];

  /**
   * Convert a modern Greek characters text to its greeklish equivalent.
   *
   * https://github.com/vbarzokas/greek-utils/blob/master/lib/index.js
   */
  static toGreeklish(text: string): string {
    return Utils.replaceText(text, Utils.greekToGreeklishMap, true, '');
  }

  /**
   * https://github.com/vbarzokas/greek-utils/blob/41ae41baf0d075a696479b9eb0fe77c475b4f3b2/lib/index.js#L83
   */
  private static replaceText(text: string, characterMap, exactMatch: boolean, ignore: string): string {
    let regexString: string;
    let regex: RegExp;

    exactMatch = exactMatch || false;

    if (typeof text === 'string' && text.length > 0) {
      characterMap.forEach(characters => {
        regexString = exactMatch ? characters.find : '[' + characters.find + ']';
        if (ignore) { regexString = '(?![' + ignore + '])' + regexString; }
        regex = new RegExp(regexString, 'g');
        text = text.replace(regex, characters.replace);
      });
    }

    return text;
  }
}
