// eagerly import theme styles so as we can override them
import '@vaadin/vaadin-lumo-styles/all-imports';

import { registerStyles, css } from '@vaadin/vaadin-themable-mixin/register-styles.js';

const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `
<custom-style>
  <style>
    html {
    }
  </style>
</custom-style>


`;

registerStyles('vaadin-grid', css`

    [part~="cell"].very_low {
       background-color: hsla(210, 40%, 85%, 0.75);
       background-image: none !important;
    }
    
    [part~="cell"].low {
       background-color: hsla(95, 40%, 85%, 0.75);
       background-image: none !important;
    }
    
    [part~="cell"].medium {
       background-color: hsla(50, 100%, 85%, 0.75);
       background-image: none !important;
    }
    
    [part~="cell"].high {
       background-color: hsla(25, 100%, 85%, 0.75);
       background-image: none !important;
    }
    
    [part~="cell"].very_high {
       background-color: hsla(5, 100%, 85%, 0.75);
       background-image: none !important;
    }
     
    [part~="header-cell"] {
        text-align: center;
    }
    
    [part~="footer-cell"] ::slotted(vaadin-grid-cell-content) {
        font-size: var(--lumo-font-size-m);
        font-weight: 500;
    }
`);

document.head.appendChild($_documentContainer.content);
