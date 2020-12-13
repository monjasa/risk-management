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
       background-color: rgba(135, 162, 199, 0.3);
    }
    
    [part~="cell"].low {
       background-color: rgba(100, 163, 56, 0.3);
    }
    
    [part~="cell"].medium {
       background-color: rgba(255, 204, 0, 0.3);
    }
    
    [part~="cell"].high {
       background-color: rgba(255, 102, 0, 0.3);
    }
    
    [part~="cell"].very_high {
       background-color: rgba(224, 59, 36, 0.3);
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
