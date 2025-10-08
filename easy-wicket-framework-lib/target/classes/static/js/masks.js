var momentFormat = 'YYYY-MM-DD';
function applyDateMask(masked) {
    var momentMask = IMask(masked, {
    mask: Date,
    pattern: momentFormat,
    lazy: false,
    overwrite: true,
  min: new Date(1970, 0, 1),
  max: new Date(2999, 0, 1),

  format: function (date) {
    return moment(date).format(momentFormat);
  },
  parse: function (str) {
    return moment(str, momentFormat);
  },

  blocks: {
    YYYY: {
      mask: IMask.MaskedRange,
      from: 1970,
      to: 2999
    },
    MM: {
      mask: IMask.MaskedRange,
      from: 1,
      to: 12
    },
    DD: {
      mask: IMask.MaskedRange,
      from: 1,
      to: 31
    }
  }
});
}

function applyNumberMask(masked,precision){
var numberMask = IMask(masked, {
  mask: Number,  // enable number mask

  // other options are optional with defaults below
  scale: precision,  // digits after point, 0 for integers
  signed: false,  // disallow negative
  thousandsSeparator: ' ',  // any single char
  padFractionalZeros: false,  // if true, then pads zeros at end to the length of scale
  normalizeZeros: true,  // appends or removes zeros at ends
  radix: '.',  // fractional delimiter
  mapToRadix: [','],  // symbols to process as radix
  lazy: false,
  overwrite: true,

  // additional number interval options (e.g.)
  min: -10000,
  max: 10000
});
}

function applyMask(masked, mask) {
    var momentMask = IMask(masked, {
    mask: Date,
    pattern: mask,
    lazy: false,
    overwrite: true,
  min: new Date(1970, 0, 1),
  max: new Date(2999, 0, 1),

  format: function (date) {
    return moment(date).format(momentFormat);
  },
  parse: function (str) {
    return moment(str, momentFormat);
  },

  blocks: {
    d: {
      mask: IMask.MaskedRange,
      from: 1,
      to: 9
    },
    a: {
      mask: IMask.MaskedRange,
      from: 1,
      to: 12
    },
    DD: {
      mask: IMask.MaskedRange,
      from: 1,
      to: 31
    }
  }
});
}